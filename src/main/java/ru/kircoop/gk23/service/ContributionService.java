package ru.kircoop.gk23.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kircoop.gk23.dao.ContributionDAO;
import ru.kircoop.gk23.dao.RentDAO;
import ru.kircoop.gk23.entity.Contribution;
import ru.kircoop.gk23.entity.Rent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Сервис для работы с периодами долгов(Contribution).
 */
@Service
public class ContributionService {

    @Autowired
    private ContributionDAO contributionDAO;

    @Autowired
    private RentDAO rentDAO;

    private static final Logger LOGGER = LoggerFactory.getLogger(ContributionService.class);

    /**
     * Метод сохранения долгов определенного года
     *
     * @param contribution период долга
     */
    @Transactional
    public void saveOrUpdate(Contribution contribution) {
        contributionDAO.saveAndFlush(contribution);
    }

    /**
     * Получение определенного долгового периода для определенного гаража.
     *
     * @param garagId Id гаража
     * @param year    год долгового периода
     * @return долговой период
     */
    public Contribution getContributionByGaragAndYear(Integer garagId, Integer year) {
        return Optional.ofNullable(contributionDAO.getContributionByGaragAndYear(garagId, year))
                .orElse(new Contribution(year));
    }

    /**
     * Метод обновления пеней(fines) для всех гаражей с включенным режимом начисления пеней(finesOn)
     */
    @Transactional
    public void updateFines() {
        //Получаем список долгов с включенным режимом пени
        for (Contribution c : contributionDAO.findByFinesOn(true)) {
            int sumContribute = c.getSumFixed(); //Находим сумму долга
            //Вычисляем кол-во дней с последнего обновления.
            //Первая дата должна устанавливаться при включении режима пеней
            LocalDateTime now = LocalDateTime.now();
            if (sumContribute != 0) {
                try {
                    // получаем кол-во дней с последнего обновления
                    long days = DAYS.between(c.getFinesLastUpdate(), now);
                    //Получаем пени за один день для текущего долга
                    double unit = sumContribute * 0.001;
                    // Получаем точную сумму пеней
                    double finesDouble = unit * days;
                    //Округляем пени до 50
                    long fines = (int) (finesDouble / 50) * 50;
                    if (fines != 0) {
                        //Вычисляем разницу дней для правильной установки даты последнего обновления пеней в связи с округлением
                        //Получаем разницу в сумме пеней
                        long difference = ((int) finesDouble % 50);
                        //Если есть остаток от округления до 50, то определяем кол-во дней для достижения этой суммы
                        int dayDifference = (difference > 0) ? (int) Math.ceil(difference / unit) : 0;
                        //Устанавливаем полученную дату после всех вычислений
                        c.setFinesLastUpdate(now.minusDays(dayDifference).toLocalDate());
                        //Суммируем существующие пени с вычисленными
                        int newFines = (int) (c.getFines() + fines);

                        if (newFines < sumContribute) {
                            c.setFines(newFines);
                        } else if (newFines == sumContribute) {
                            c.setFines(newFines);
                            c.setFinesOn(false);
                        } else {
                            //Если новые пени больши суммы долгов определяем сумму начислений этого года и отключаем пени
                            c.setFinesOn(false);
                            if (c.getFines() == 0) {
                                c.setFines(sumContribute);
                            }
                        }
                    }
                } catch (ArithmeticException e) {
                    LOGGER.error("Арифметическая ошибка для contribution_id=" + c.getId());
                }
            } else {
                c.setFinesOn(false); // если сумма долга равна 0, то режим начисления пеней выключается.
            }
            contributionDAO.save(c); //сохраняем долговой период
        }
    }

    /**
     * Метод включения режима начисления пеней
     *
     * @param now Текущая дата
     */
    public void onFines(Calendar now) {
        Rent rent = rentDAO.findByYearRent(now.get(Calendar.YEAR));
        //Дата для 1 января текущего года
        LocalDate newYear = LocalDate.of(now.get(Calendar.YEAR), 1, 1);
        //Дата для 1 июля текущего года
        LocalDate july = LocalDate.of(now.get(Calendar.YEAR), 7, 1);

        //Включение пеней для должников со следующего года
        for (Contribution c : contributionDAO.findContributionsByFines(now.get(Calendar.YEAR) - 1)) {
            c.setFinesOn(true);
            c.setFinesLastUpdate(newYear);
            contributionDAO.save(c);
        }
        //Включение пеней для должников не уплативших до 1 июля
        if (now.get(Calendar.MONTH) >= Calendar.JULY) {
            for (Contribution c : contributionDAO.findContributionsByFines(now.get(Calendar.YEAR))) {
                if (getRentMax(rent, c).equals(c.getSumFixed())) {
                    c.setFinesOn(true);
                    c.setFinesLastUpdate(july);
                    contributionDAO.save(c);
                }
            }
        }
    }


    private Integer getRentMax(Rent rent, Contribution c) {
        int rentMax = 0;
        if (!c.isMemberBoardOn()) {
            rentMax += rent.getContributeMax();
        }
        if (c.isBenefitsOn()) {
            rentMax += rent.getContLandMax() / 2;
        } else {
            rentMax += rent.getContLandMax();
        }
        rentMax += rent.getContTargetMax();
        return rentMax;
    }
}
