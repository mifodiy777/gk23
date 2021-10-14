package ru.kircoop.gk23.dto;

import lombok.Data;

@Data
public class PaymentView {

    private Integer id;
    private Integer garagId;
    private String garagSeries;
    private String garagNumber;
    private Integer number;
    private Integer year;
    private String datePayment;
    private String fio;
    private String fullNameGarag;
    private int total;
    private Integer pay;
    private int contributePay;
    private int contLandPay;
    private int contTargetPay;
    private int finesPay;
    private int additionallyPay;
    private int oldContributePay;
    private int debtPastPay;
}
