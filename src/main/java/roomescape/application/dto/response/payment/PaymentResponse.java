package roomescape.application.dto.response.payment;

import roomescape.domain.payment.Payment;

public record PaymentResponse(
        int totalAmount,
        String paymentKey,
        String orderId,
        String status,
        String requestedAt,
        String approvedAt
) {
    public Payment toPayment() {
        return new Payment(totalAmount, paymentKey, orderId, requestedAt, approvedAt);
    }
}
