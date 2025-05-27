package roomescape.reservation.presentation.dto;

public record PaymentRequest(
        String paymentKey,
        String orderId,
        int amount
) {
}
