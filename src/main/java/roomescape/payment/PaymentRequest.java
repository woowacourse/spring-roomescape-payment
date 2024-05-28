package roomescape.payment;

public record PaymentRequest(
        long amount,
        String orderId,
        String paymentKey
) {
}
