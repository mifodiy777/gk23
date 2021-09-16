package ru.kircoop.gk23.dto;

import lombok.Data;

@Data
public class PaymentView {

    private Integer id;
    private Integer garagId;
    private Integer number;
    private Integer year;
    private String datePayment;
    private String fio;
    private int pay;
    private int contributePay;
    private int contLandPay;
    private int contTargetPay;
    private int finesPay;
    private int additionallyPay;
    private int oldContributePay;
    private int debtPastPay;

    public int getSumPay() {
        return pay + contributePay + contLandPay + contTargetPay + additionallyPay + finesPay;
    }
}
