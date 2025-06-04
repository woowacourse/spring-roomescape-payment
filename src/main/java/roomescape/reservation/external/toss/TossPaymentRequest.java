package roomescape.reservation.external.toss;

public record TossPaymentRequest(
        String paymentKey,
        Long amount,
        String orderId
) {
}
