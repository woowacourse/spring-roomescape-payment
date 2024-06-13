package roomescape.service.payment.dto;

public record TossPaymentResponse(
    String paymentKey,
    String totalAmount,
    String orderId,
    String status,
    String requestedAt,
    String approvedAt
) {

}
