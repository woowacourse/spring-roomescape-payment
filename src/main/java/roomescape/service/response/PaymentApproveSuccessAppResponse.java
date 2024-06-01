package roomescape.service.response;

public record PaymentApproveSuccessAppResponse(
        String paymentKey,
        String orderId,
        Long totalAmount,
        String status,
        String requestedAt,
        String approvedAt
) {
}
