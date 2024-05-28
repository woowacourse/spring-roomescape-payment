package roomescape.payment.dto;

public record PaymentResponse(
        String paymentKey,
        String status,
        String orderId,
        String orderName,
        Long requestedAt,
        Long approvedAt,
        Integer totalAmount,
        Integer balanceAmount
) {
}
