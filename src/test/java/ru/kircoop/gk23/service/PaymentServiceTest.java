package ru.kircoop.gk23.service;


import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.kircoop.gk23.dao.PaymentDAO;
import ru.kircoop.gk23.entity.Contribution;
import ru.kircoop.gk23.entity.Garag;
import ru.kircoop.gk23.entity.Payment;
import ru.kircoop.gk23.entity.Person;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.*;

/**
 * Created by Кирилл on 20.03.2017.
 */
public class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService = new PaymentService();

    @Mock
    private PaymentDAO paymentDAO;

    @Mock
    private ContributionService contributionService;

    @Mock
    private GaragService garagService;

    @BeforeClass
    public void setUp() {
        initMocks(this);
    }

    @BeforeMethod
    public void init() {
        reset(paymentDAO);
    }

    /**
     * Получение списка периодов для которых существуют платежи
     *
     * @throws Exception
     */
    @Test
    public void testFindYears() throws Exception {
        paymentService.findYears();
        verify(paymentDAO).findYears();
    }

    /**
     * Сохранение платежа
     *
     * @throws Exception
     */
    @Test
    public void testSaveOrUpdate() throws Exception {
        paymentService.saveOrUpdate(new Payment());
        verify(paymentDAO).saveAndFlush(any(Payment.class));
    }

    /**
     * Получение списка платежей определенного года
     *
     * @throws Exception
     */
    @Test
    public void testFindByYear() throws Exception {
        paymentService.findByYear(2014);
        verify(paymentDAO).findByYear(2014);
    }

    /**
     * Получение платежа по идентификатору
     *
     * @throws Exception
     */
    @Test
    public void testGetPayment() throws Exception {
        paymentService.getPayment(100);
        verify(paymentDAO).getById(100);
    }

    /**
     * Удаление платежа
     *
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
        paymentService.delete(100);
        verify(paymentDAO).deleteById(100);
    }

    /**
     * Возвращает платежи для определенного гаража с остатками денег
     *
     * @throws Exception
     */
    @Test
    public void testGetPaymentOnGarag() throws Exception {
        Garag garag = new Garag();
        garag.setId(100);
        paymentService.getPaymentOnGarag(garag);
        verify(paymentDAO).getPaymentOnGarag(100);
    }

    /**
     * Метод проведения дополнительного платежа
     *
     * @throws Exception
     */
    @Test
    public void testPayAdding() throws Exception {
        LocalDate now = LocalDate.now();
        Payment payment = new Payment();
        payment.setPay(1000);
        Garag garag = new Garag();
        garag.setId(1);
        Person person = mock(Person.class);
        garag.setPerson(person);
        payment.setGarag(garag);
        String fio = "Иванов Иван Иваныч";
        given(person.getFIO()).willReturn(fio);
        given(garagService.getGarag(garag.getId())).willReturn(garag);
        given(paymentDAO.getMaxValueNumber()).willReturn(1);
        given(garagService.sumContribution(garag)).willReturn(100);

        paymentService.pay(payment, false, "adding");

        assertEquals(payment.getNumber(), Integer.valueOf(2));
        assertEquals(payment.getYear().intValue(), now.getYear());
        assertEquals(payment.getDebtPastPay(), 100);
        assertEquals(payment.getFio(), fio);
        assertEquals(payment.getAdditionallyPay(), 1000);
        assertEquals(payment.getPay(), 0);
        assertNotNull(payment.getDatePayment());

        verify(paymentDAO).getMaxValueNumber();
        verify(paymentDAO).save(payment);
    }

    /**
     * Метод проведения платежа по долгам прощлых лет
     * Сумма долга больше суммы платежа
     *
     * @throws Exception
     */
    @Test
    public void testOldPayMore() throws Exception {
        LocalDate now = LocalDate.now();
        Payment payment = new Payment();
        Contribution contribution = new Contribution();
        payment.setPay(1000);

        headerPay(payment, now, contribution, "oldPay");

        paymentService.pay(payment, false, "default");

        assertEquals(payment.getOldContributePay(), 800);
        assertEquals(payment.getGarag().getOldContribute(), 0);
        assertEquals(payment.getPay(), 200);
    }

    /**
     * Метод проведения платежа по долгам прощлых лет
     * Сумма долга равна сумме платежа
     *
     * @throws Exception
     */
    @Test
    public void testOldPay() throws Exception {
        LocalDate now = LocalDate.now();
        Payment payment = new Payment();
        Contribution contribution = new Contribution();
        payment.setPay(800);

        headerPay(payment, now, contribution, "oldPay");

        paymentService.pay(payment, false, "default");

        assertEquals(payment.getOldContributePay(), 800);
        assertEquals(payment.getGarag().getOldContribute(), 0);
        assertEquals(payment.getPay(), 0);
    }


    /**
     * Метод проведения платежа moreDebts
     *
     * @throws Exception
     */
    @Test
    public void testPayMoreDebts() throws Exception {
        LocalDate now = LocalDate.now();
        Payment payment = new Payment();
        payment.setPay(3000);

        Contribution contribution = new Contribution();
        contribution.setYear(2015);
        contribution.setContribute(1000);
        contribution.setContLand(200);
        contribution.setContTarget(1000);
        contribution.setFines(300);

        headerPay(payment, now, contribution, "default");

        paymentService.pay(payment, false, "default");

        assertEquals(contribution.getContribute(), 0);
        assertEquals(contribution.getContLand(), 0);
        assertEquals(contribution.getContTarget(), 0);
        assertEquals(contribution.getFines(), 0);
        assertFalse(contribution.isFinesOn());

        assertEquals(payment.getContributePay(), 1000);
        assertEquals(payment.getContLandPay(), 200);
        assertEquals(payment.getContTargetPay(), 1000);
        assertEquals(payment.getFinesPay(), 300);
        assertEquals(payment.getPay(), 500);

        verify(paymentDAO).getMaxValueNumber();
        verify(paymentDAO).save(payment);
        verify(contributionService).saveOrUpdate(contribution);
    }

    /**
     * Метод проведения платежа moreDebts - частичное погашение пеней
     *
     * @throws Exception
     */
    @Test
    public void testPayMoreDebtsPart() throws Exception {
        LocalDate now = LocalDate.now();

        Payment payment = new Payment();
        payment.setPay(3000);

        Contribution contribution = new Contribution();
        contribution.setYear(2015);
        contribution.setContribute(1000);
        contribution.setContLand(200);
        contribution.setContTarget(1000);
        contribution.setFines(1000);

        headerPay(payment, now, contribution, "default");

        paymentService.pay(payment, false, "default");

        assertEquals(contribution.getContribute(), 0);
        assertEquals(contribution.getContLand(), 0);
        assertEquals(contribution.getContTarget(), 0);
        assertEquals(contribution.getFines(), 200);

        assertEquals(payment.getContributePay(), 1000);
        assertEquals(payment.getContLandPay(), 200);
        assertEquals(payment.getContTargetPay(), 1000);
        assertEquals(payment.getFinesPay(), 800);
        assertEquals(payment.getPay(), 0);

        verify(paymentDAO).getMaxValueNumber();
        verify(paymentDAO).save(payment);
//        verify(contributionService).saveOrUpdate(contribution);
    }

    /**
     * Метод проведения платежа lessDebts - частичное погашение целевого долга
     *
     * @throws Exception
     */
    @Test
    public void testPayLessDebtsTarget() throws Exception {
        LocalDate now = LocalDate.now();

        Payment payment = new Payment();
        payment.setPay(2000);

        Contribution contribution = new Contribution();
        contribution.setYear(now.getYear());
        contribution.setContribute(1000);
        contribution.setContLand(200);
        contribution.setContTarget(1000);
        contribution.setFines(200);

        headerPay(payment, now, contribution, "default");

        paymentService.pay(payment, false, "default");

        assertEquals(contribution.getContribute(), 0);
        assertEquals(contribution.getContLand(), 0);
        assertEquals(contribution.getContTarget(), 200);
        assertEquals(contribution.getFines(), 200);

        assertEquals(payment.getContributePay(), 1000);
        assertEquals(payment.getContLandPay(), 200);
        assertEquals(payment.getContTargetPay(), 800);
        assertEquals(payment.getFinesPay(), 0);
        assertEquals(payment.getPay(), 0);
    }

    /**
     * Метод проведения платежа lessDebts - частичное погашение аренды земли
     *
     * @throws Exception
     */
    @Test
    public void testPayLessDebtsLand() throws Exception {
        LocalDate now = LocalDate.now();

        Payment payment = new Payment();
        payment.setPay(1100);

        Contribution contribution = new Contribution();
        contribution.setYear(now.getYear());
        contribution.setContribute(1000);
        contribution.setContLand(200);
        contribution.setContTarget(1000);
        contribution.setFines(200);

        headerPay(payment, now, contribution, "default");

        paymentService.pay(payment, false, "default");

        assertEquals(contribution.getContribute(), 0);
        assertEquals(contribution.getContLand(), 100);
        assertEquals(contribution.getContTarget(), 1000);
        assertEquals(contribution.getFines(), 200);

        assertEquals(payment.getContributePay(), 1000);
        assertEquals(payment.getContLandPay(), 100);
        assertEquals(payment.getContTargetPay(), 0);
        assertEquals(payment.getFinesPay(), 0);
        assertEquals(payment.getPay(), 0);
    }

    /**
     * Метод проведения платежа lessDebts - частичное погашение основного взноса
     *
     * @throws Exception
     */
    @Test
    public void testPayLessDebtsContribute() throws Exception {
        LocalDate now = LocalDate.now();

        Payment payment = new Payment();
        payment.setPay(800);

        Contribution contribution = new Contribution();
        contribution.setYear(now.getYear());
        contribution.setContribute(1000);
        contribution.setContLand(200);
        contribution.setContTarget(1000);
        contribution.setFines(200);

        headerPay(payment, now, contribution, "default");

        paymentService.pay(payment, false, "default");

        assertEquals(contribution.getContribute(), 200);
        assertEquals(contribution.getContLand(), 200);
        assertEquals(contribution.getContTarget(), 1000);
        assertEquals(contribution.getFines(), 200);

        assertEquals(payment.getContributePay(), 800);
        assertEquals(payment.getContLandPay(), 0);
        assertEquals(payment.getContTargetPay(), 0);
        assertEquals(payment.getFinesPay(), 0);
        assertEquals(payment.getPay(), 0);
    }

    private void headerPay(Payment payment, LocalDate now, Contribution contribution, String type) {
        Garag garag = new Garag();
        Person person = mock(Person.class);
        payment.setDatePayment(now);
        garag.setId(1);
        garag.setPerson(person);
        payment.setGarag(garag);
        List<Contribution> list = new ArrayList<>();
        list.add(contribution);
        garag.setContributions(list);
        if (type.equals("oldPay")) {
            garag.setOldContribute(800);
        }
        given(person.getFIO()).willReturn("Иванов Иван Иваныч");
        given(garagService.getGarag(garag.getId())).willReturn(garag);
        given(paymentDAO.getMaxValueNumber()).willReturn(null);
        given(garagService.sumContribution(garag)).willReturn(100);

    }
}