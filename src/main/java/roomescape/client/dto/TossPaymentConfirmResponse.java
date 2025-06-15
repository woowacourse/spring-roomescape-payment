package roomescape.client.dto;

import roomescape.domain.payment.Payment;

public record TossPaymentConfirmResponse(
        String paymentKey,
        String orderId,
        Long totalAmount
) {
    public Payment toPayment() {
        return new Payment(paymentKey, orderId, totalAmount);
    }
}
