package roomescape.payment.dto.response;

public record PaymentResponse(
    String paymentKey,
    String orderId
) {
}
