package roomescape.application.dto.response.payment;

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
        return new Payment(totalAmount, paymentKey, orderId, requestedAt, approvedAt);
    }
}
