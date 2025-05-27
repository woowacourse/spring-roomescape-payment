package roomescape.reservation.service;

public record TossPaymentConfirmRequest(
        String orderId,
        int amount,
        String paymentKey
) {
}
