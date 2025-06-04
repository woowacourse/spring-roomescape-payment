package roomescape.payment.infrastructure.dto;

public record PaymentResponse(
    String paymentKey,
    String orderId
) {
}
