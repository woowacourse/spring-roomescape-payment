package roomescape.reservation.application.dto;

public record PaymentRequest(
        Long amount,
        String orderId,
        String paymentKey
) {
}
