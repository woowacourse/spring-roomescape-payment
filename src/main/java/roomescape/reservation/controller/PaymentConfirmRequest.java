package roomescape.reservation.controller;

public record PaymentConfirmRequest(
        String paymentKey,
        String orderId,
        int amount
) {
}
