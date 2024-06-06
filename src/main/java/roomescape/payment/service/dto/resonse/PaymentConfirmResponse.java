package roomescape.payment.service.dto.resonse;

public record PaymentConfirmResponse(
        String paymentKey,
        String orderId,
        long totalAmount,
        String orderName,
        String status,
        String requestedAt,
        String approvedAt
) {
}
