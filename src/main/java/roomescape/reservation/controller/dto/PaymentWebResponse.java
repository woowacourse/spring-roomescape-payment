package roomescape.reservation.controller.dto;

import roomescape.payment.domain.Payment;

public record PaymentWebResponse(String paymentKey, int amount) {

    public PaymentWebResponse(Payment payment) {
        this(payment.getPaymentKey(), payment.getAmount());
    }
}
