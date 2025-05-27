package roomescape.reservation.controller;

public record TossPaymentConfirmResponse(
        String paymentKey,
        String orderId,
        int totalAmount,
        String status
) {
}
