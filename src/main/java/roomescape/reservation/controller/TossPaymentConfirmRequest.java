package roomescape.reservation.controller;

public record TossPaymentConfirmRequest(
        String paymentKey,
        String orderId,
        int amount
) {
}
