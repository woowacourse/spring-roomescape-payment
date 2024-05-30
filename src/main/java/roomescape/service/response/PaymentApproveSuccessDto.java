package roomescape.service.response;

public record PaymentApproveSuccessDto(
        String paymentKey,
        String orderId,
        Long totalAmount,
        String status,
        String requestedAt,
        String approvedAt
) {
}
