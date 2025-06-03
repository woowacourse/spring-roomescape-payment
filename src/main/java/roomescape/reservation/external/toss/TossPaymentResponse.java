package roomescape.reservation.external.toss;

public record TossPaymentResponse(
        String orderId,
        Long totalAmount,
        String paymentKey
) {
}
