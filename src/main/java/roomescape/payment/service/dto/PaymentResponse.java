package roomescape.payment.service.dto;

import roomescape.payment.domain.Payment;

public record PaymentResponse(
        String paymentKey,
        Long amount
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(payment.getPaymentKey(), payment.getAmount());
    }
}
