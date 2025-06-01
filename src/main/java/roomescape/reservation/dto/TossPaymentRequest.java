package roomescape.reservation.dto;

public record TossPaymentRequest(
        String paymentKey,
        String orderId,
        int amount
) {
}
