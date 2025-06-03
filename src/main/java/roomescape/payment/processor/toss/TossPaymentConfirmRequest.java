package roomescape.payment.processor.toss;

public record TossPaymentConfirmRequest(
        int amount,
        String orderId,
        String paymentKey
) {
}
