package roomescape.reservation.service.dto;

import roomescape.payment.domain.Payment;

import java.math.BigDecimal;

public record PaymentInfo(String paymentKey, BigDecimal paymentAmount) {
    public static final PaymentInfo NOT_PAYMENT = new PaymentInfo("", BigDecimal.ZERO);

    public static PaymentInfo from(Payment payment) {
        return new PaymentInfo(payment.getPaymentKey(), payment.getAmountAsValue());
    }
}
