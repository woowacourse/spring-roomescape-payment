package roomescape.payment.service.dto;

import roomescape.payment.domain.Payment;

public record ConfirmPaymentResponse(
        Integer totalAmount,
        String paymentKey,
        PaymentFailure failure
) {
    public Payment toEntity() {
        return new Payment(paymentKey, totalAmount);
    }
}
