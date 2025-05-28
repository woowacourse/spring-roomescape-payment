package roomescape.reservation.controller;

public record PaymentConfirmResponse(
        String paymentKey,
        String orderId,
        int totalAmount,
        String status
) {
}
