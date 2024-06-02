package roomescape.dto.service;

import roomescape.domain.Payment;

public record TossPaymentResponse(
        String paymentKey,
        String orderId,
        Long totalAmount
) {
    public Payment toPayment() {
        return new Payment(null, paymentKey, orderId, totalAmount);
    }
}
