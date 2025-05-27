package roomescape.reservation.service;

public record TossPaymentConfirmResponse(
        String orderId,
        String paymentKey
) {
}
