package ru.kircoop.gk23.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kircoop.gk23.dao.PaymentDAO;
import ru.kircoop.gk23.entity.Contribution;
import ru.kircoop.gk23.entity.Garag;
import ru.kircoop.gk23.entity.Payment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentDAO paymentDAO;

    @Autowired
    private ContributionService contributionService;

    @Autowired
    private GaragService garagService;

    /**
     * Получение списка периодов для которых существуют платежи
     *
     * @return список периодов
     */
    public List<Integer> findYears() {
        return paymentDAO.findYears();
    }

    /**
     * Сохранение платежа
     *
     * @param payment Платеж
     * @return сохраненный платеж
     */
    @Transactional
    public Payment saveOrUpdate(Payment payment) {
        return paymentDAO.saveAndFlush(payment);
    }

    /**
     * Получение списка платежей определенного года
     *
     * @param year год
     * @return Список платежей
     */
    public List<Payment> findByYear(Integer year) {
        return paymentDAO.findByYear(year);
    }

    /**
     * Получение платежа по идентификатору
     *
     * @param id ID платежа
     * @return платеж
     */
    public Payment getPayment(Integer id) {
        return paymentDAO.getById(id);
    }

    /**
     * Удаление платежа
     *
     * @param id ID платежа
     */
    @Transactional
    public void delete(Integer id) {
        paymentDAO.deleteById(id);
    }

    /**
     * Возвращает платежи для определенного гаража с остатками денег
     *
     * @param garag Гараж
     * @return список платежей
     */
    public List<Payment> getPaymentOnGaragNotNullPay(Garag garag) {
        return paymentDAO.getPaymentOnGaragNotNullPay(garag.getId());
    }

    /**
     * Возвращает платежи для определенного гаража
     *
     * @param garag Гараж
     * @return список платежей
     */
    public List<Payment> getPaymentOnGarag(Garag garag) {
        return paymentDAO.getPaymentOnGarag(garag.getId());
    }

    /**
     * Метод проведения платежа, или при раскидывания остаточных средств при создании нового долгового периода
     *
     * @param payment           Платеж
     * @param isCreateNewPeriod true - вызов текущего метода при создании нового периода.
     * @param type              Тип платежа
     * @return Платеж
     */
    @Transactional
    public Payment pay(Payment payment, Boolean isCreateNewPeriod, String type) {
        Garag garag = payment.getGarag();
        if (!isCreateNewPeriod) {
            setHeaderPay(payment, garag);
        }
        if (type.equals("adding")) {
            return addingPay(payment);
        }
        distributionOldContribute(payment);
        for (Contribution c : garag.getContributions()) {
            if (payment.getPay() == 0) {
                break;
            }
            if (c.getSumFixed() + c.getFines() == 0) {
                continue;
            }
            if (payment.getPay() >= c.getSumFixed()) {
                moreDebts(payment, c);
            } else {
                lessDebts(payment, c);
            }
            contributionService.saveOrUpdate(c);
        }
        payment.setDebtPastPay(garagService.sumContribution(payment.getGarag()));
        return paymentDAO.save(payment);
    }

    /**
     * Вычисление номера чека - Max
     *
     * @return свободный максимальный номер чека
     */
    private Integer getMaxNumber() {
        Integer number = paymentDAO.getMaxValueNumber();
        return (number == null) ? 1 : number + 1;
    }

    /**
     * Шапка платежа
     *
     * @param payment платеж
     * @param garag   гараж
     */
    private void setHeaderPay(Payment payment, Garag garag) {
        if (payment.getDatePayment() == null) {
            payment.setDatePayment(LocalDate.now());
            payment.setYear(LocalDateTime.now().getYear());
        } else {
            payment.setYear(payment.getDatePayment().getYear());
        }
        //Назначили номер платежа
        payment.setNumber(getMaxNumber());
        payment.setGarag(garag);
        payment.setFio(garag.getPerson().getFIO());
    }

    /**
     * Проводка  дополнительного платежа
     *
     * @param payment платеж
     * @return сохраненный платеж
     */
    private Payment addingPay(Payment payment) {
        payment.setAdditionallyPay(payment.getPay());
        payment.setPay(0);
        payment.setDebtPastPay(garagService.sumContribution(payment.getGarag()));
        return paymentDAO.save(payment);
    }

    /**
     * Распределение платежа на оплату долгов прошлых лет(не облагаемых пенями)
     *
     * @param payment Платеж
     */
    private void distributionOldContribute(Payment payment) {
        int oldContribute = payment.getGarag().getOldContribute();
        Garag garag = payment.getGarag();
        if (oldContribute != 0) {
            if (payment.getPay() <= oldContribute) {
                payment.setOldContributePay(payment.getPay());
                garag.setOldContribute(oldContribute - payment.getPay());
                payment.setPay(0);
            } else {
                payment.setOldContributePay(oldContribute);
                payment.setPay(payment.getPay() - oldContribute);
                garag.setOldContribute(0);
            }
            garagService.save(garag);
        }
    }

    /**
     * Распределение платежа когда уплаченных денег больше чем долг за выбранный период
     *
     * @param payment платеж
     * @param c       период
     */
    private void moreDebts(Payment payment, Contribution c) {
        Integer reminder = c.getSumFixed();
        payment.setContributePay(payment.getContributePay() + c.getContribute());
        c.setContribute(0);
        payment.setContLandPay(payment.getContLandPay() + c.getContLand());
        c.setContLand(0);
        payment.setContTargetPay(payment.getContTargetPay() + c.getContTarget());
        c.setContTarget(0);
        c.setFinesOn(false);
        payment.setPay(payment.getPay() - reminder);
        //Если взнос уплачен проверяем оплату по пеням.
        if (payment.getPay() != 0) {
            if (c.getFines() >= 0 && payment.getPay() >= c.getFines()) {
                payment.setFinesPay(payment.getFinesPay() + c.getFines());
                payment.setPay(payment.getPay() - c.getFines());
                c.setFines(0);
            } else {
                payment.setFinesPay(payment.getFinesPay() + payment.getPay());
                c.setFines(c.getFines() - payment.getPay());
                payment.setPay(0);
            }
        }
    }

    /**
     * Распределение платежа когда уплаченных денег меньше чем долг за выбранный период
     *
     * @param payment платеж
     * @param c       период
     */
    private void lessDebts(Payment payment, Contribution c) {
        if (c.getContribute() > payment.getPay()) {
            payment.setContributePay(payment.getContributePay() + payment.getPay());
            c.setContribute(c.getContribute() - payment.getPay());
            payment.setPay(0);
        } else {
            payment.setPay(payment.getPay() - c.getContribute());
            payment.setContributePay(payment.getContributePay() + c.getContribute());
            c.setContribute(0);
        }
        if (c.getContLand() > payment.getPay()) {
            payment.setContLandPay(payment.getContLandPay() + payment.getPay());
            c.setContLand(c.getContLand() - payment.getPay());
            payment.setPay(0);
        } else {
            payment.setPay(payment.getPay() - c.getContLand());
            payment.setContLandPay(payment.getContLandPay() + c.getContLand());
            c.setContLand(0);
        }
        if (c.getContTarget() > payment.getPay()) {
            payment.setContTargetPay(payment.getContTargetPay() + payment.getPay());
            c.setContTarget(c.getContTarget() - payment.getPay());
            payment.setPay(0);
        }
        //Для текущего года при частичной оплате в любом месяце пени выключаются
        if (c.getYear().equals(payment.getYear())) {
            c.setFinesOn(false);
        }
    }
}

