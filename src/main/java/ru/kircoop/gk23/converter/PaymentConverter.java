package ru.kircoop.gk23.converter;

import org.springframework.stereotype.Service;
import ru.kircoop.gk23.dto.PaymentView;
import ru.kircoop.gk23.entity.Garag;
import ru.kircoop.gk23.entity.Payment;

import java.time.LocalDate;

import static ru.kircoop.gk23.utils.DateUtils.DD_MM_YYYY_DOT;

@Service
public class PaymentConverter {

    public PaymentView map(Payment payment) {
        if (payment == null) return null;
        PaymentView dto = new PaymentView();
        dto.setId(payment.getId());
        dto.setGaragId(payment.getGarag().getId());
        dto.setNumber(payment.getNumber());
        dto.setYear(payment.getYear());
        dto.setDatePayment(payment.getDatePayment().format(DD_MM_YYYY_DOT));
        dto.setFio(payment.getFio());
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