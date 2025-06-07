package roomescape.reservation.external.toss;

public record TossPaymentRequest(
        String orderId,
        Long amount,
        String paymentKey
) {
}
