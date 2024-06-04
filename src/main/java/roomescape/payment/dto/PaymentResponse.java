package roomescape.payment.dto;

public record PaymentResponse(
        String paymentKey,
        String orderId,
        Long totalAmount,
        String method,
        String requestedAt,
        String approvedAt
) {
}
