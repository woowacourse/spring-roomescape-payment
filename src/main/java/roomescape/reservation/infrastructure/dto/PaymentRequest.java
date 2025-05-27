package roomescape.reservation.infrastructure.dto;

public record PaymentRequest(
        Long amount,
        String orderId,
        String paymentKey
) {
}
