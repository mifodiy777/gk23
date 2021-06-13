package ru.kircoop.gk23.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

/*Класс платежа*/
@Entity
public class Payment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_payment", nullable = false)
    private Integer id;

    //Номер платежа
    @Column(name = "number_payment", nullable = false, unique = true)
    private Integer number;

    @Column(name = "year", nullable = false)
    private Integer year;

    /*Дата платежа*/
    @Column(name = "date_payment", nullable = false)
    private Calendar datePayment;

    //ФИО платильщика
    @Column(name = "person_fio", nullable = false)
    private String fio;

    //Сумма платежа
    @Column(name = "payment")
    private int pay;

    //Платеж в членский взнос
    @Column(name = "contribute_pay")
    private int contributePay;

    //Платеж в аренду земли
    @Column(name = "cont_land_pay")
    private int contLandPay;

    //Платеж в целевой взнос
    @Column(name = "cont_target_pay")
    private int contTargetPay;

    //Платеж в пени
    @Column(name = "fines_pay")
    private int finesPay;

    //Платеж в дополнительные взносы
    @Column(name = "additionally_pay")
    private int additionallyPay;

    //Платеж в долги прошлых лет
    @Column(name = "old_contribute_pay")
    private int oldContributePay;

    //Долг после оплаты(Для повторного печати чека)
    @Column(name = "debt_past_pay")
    private int debtPastPay;

    public Payment() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Calendar getDatePayment() {
        return datePayment;
    }

    public void setDatePayment(Calendar datePayment) {
        this.datePayment = datePayment;
    }

    public String getDatePay() {
        SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy");
        return fmt.format(this.datePayment.getTime());
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public int getPay() {
        return pay;
    }

    public void setPay(int pay) {
        this.pay = pay;
    }

    public int getSumPay() {
        return this.pay + this.contributePay + this.contLandPay + this.contTargetPay + this.additionallyPay + this.finesPay + this.oldContributePay;
    }

    public int getContributePay() {
        return contributePay;
    }

    public void setContributePay(int contributePay) {
        this.contributePay = contributePay;
    }

    public int getContLandPay() {
        return contLandPay;
    }

    public void setContLandPay(int contLandPay) {
        this.contLandPay = contLandPay;
    }

    public int getContTargetPay() {
        return contTargetPay;
    }

    public void setContTargetPay(int contTargetPay) {
        this.contTargetPay = contTargetPay;
    }

    public int getFinesPay() {
        return finesPay;
    }

    public void setFinesPay(int finesPay) {
        this.finesPay = finesPay;
    }

    public int getAdditionallyPay() {
        return additionallyPay;
    }

    public void setAdditionallyPay(int additionallyPay) {
        this.additionallyPay = additionallyPay;
    }

    public int getOldContributePay() {
        return oldContributePay;
    }

    public void setOldContributePay(int oldContributePay) {
        this.oldContributePay = oldContributePay;
    }

    public int getDebtPastPay() {
        return debtPastPay;
    }

    public void setDebtPastPay(int debtPastPay) {
        this.debtPastPay = debtPastPay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(number, payment.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}
