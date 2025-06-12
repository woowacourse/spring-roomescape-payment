package roomescape.payment.service.dto;

public record PaymentClientResponse(
    String paymentKey,
    String orderId,
    Long totalAmount,
    String status,
    String requestedAt,
    String approvedAt
) {

}
