package roomescape.payment.dto;

import roomescape.payment.domain.Payment;

public record PaymentResponse(
        String paymentKey,
        int amount
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getPaymentKey(),
                payment.getAmount()
        );
    }
}
