package roomescape.service.response;

public record PaymentApproveSuccessAppResponse(
        String paymentKey,
        String orderId,
        String totalAmount,
        String status,
        String requestedAt,
        String approvedAt
) {
}
