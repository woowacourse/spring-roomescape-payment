package roomescape.application.dto.response.payment;

public record PaymentResponse(
        String paymentKey,
        String orderId,
        String status,
        String requestedAt,
        String approvedAt
) {
}
