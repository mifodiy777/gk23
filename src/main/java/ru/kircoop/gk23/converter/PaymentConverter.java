package ru.kircoop.gk23.converter;

import org.springframework.stereotype.Service;
import ru.kircoop.gk23.dto.PaymentView;
import ru.kircoop.gk23.entity.Garag;
import ru.kircoop.gk23.entity.Payment;

import java.time.LocalDate;
import java.util.Optional;

import static ru.kircoop.gk23.utils.DateUtils.DD_MM_YYYY_DOT;

@Service
public class PaymentConverter {

    public PaymentView map(Payment payment) {
        if (payment == null) return null;
        PaymentView dto = new PaymentView();
        dto.setId(payment.getId());
        dto.setGaragId(payment.getGarag().getId());
        dto.setGaragSeries(payment.getGarag().getSeries());
        dto.setGaragNumber(payment.getGarag().getNumber());
        dto.setNumber(payment.getNumber());
        dto.setYear(payment.getYear());
        dto.setDatePayment(payment.getDatePayment().format(DD_MM_YYYY_DOT));
        dto.setFio(payment.getFio());
        dto.setFullNameGarag(payment.getGarag().getFullName());
        dto.setTotal(Optional.ofNullable(payment.getTotal()).orElse(payment.getSumPay()));
        dto.setPay(payment.getPay());
        dto.setContributePay(payment.getContributePay());
        dto.setContLandPay(payment.getContLandPay());
        dto.setContTargetPay(payment.getContTargetPay());
        dto.setFinesPay(payment.getFinesPay());
        dto.setAdditionallyPay(payment.getAdditionallyPay());
        dto.setOldContributePay(payment.getOldContributePay());
        dto.setDebtPastPay(payment.getDebtPastPay());
        return dto;
    }

    public Payment fromView(PaymentView dto, Garag g) {
        if (dto == null) return null;
        Payment payment = new Payment();
        payment.setGarag(g);
        payment.setPay(dto.getPay());
        return payment;
    }
}
