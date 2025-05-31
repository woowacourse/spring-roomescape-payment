package roomescape.payment;

public record PaymentConfirmRequest(
        String paymentKey,
        String orderId,
        int amount
) {
}
