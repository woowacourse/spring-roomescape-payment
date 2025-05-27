package roomescape.reservation.domain;

public record PaymentInfo(
        String orderId,
        Long totalAmount,
        String paymentKey
) {
}
