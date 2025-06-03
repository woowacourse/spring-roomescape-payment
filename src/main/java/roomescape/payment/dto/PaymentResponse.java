package roomescape.payment.dto;

public record PaymentResponse(
    String paymentKey,
    String orderId
) {
}
