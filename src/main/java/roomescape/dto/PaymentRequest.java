package roomescape.dto;

public record PaymentRequest(
        long reservationId,
        String paymentKey,
        String orderId,
        long amount
) {
}
