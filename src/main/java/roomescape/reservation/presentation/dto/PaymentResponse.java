package roomescape.reservation.presentation.dto;

public record PaymentResponse(
    String paymentKey,
    String orderId
) {
}
