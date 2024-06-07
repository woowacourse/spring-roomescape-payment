package roomescape.infrastructure.payment.dto;

import roomescape.domain.payment.Payment;

public record PaymentResponse(
        Long totalAmount,
        String paymentKey,
        String orderId,
        String status,
        String requestedAt,
        String approvedAt
) {

    public static PaymentResponse empty() {
        return new PaymentResponse(0L, "", "", "", "", "");
    }

    public Payment toPayment() {
        return new Payment(paymentKey, totalAmount, orderId, requestedAt, approvedAt);
    }
}
