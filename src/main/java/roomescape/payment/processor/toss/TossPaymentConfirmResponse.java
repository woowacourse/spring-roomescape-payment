package roomescape.payment.processor.toss;

public record TossPaymentConfirmResponse(
        String orderId,
        String paymentKey
) {
}
