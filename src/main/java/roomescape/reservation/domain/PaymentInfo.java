package roomescape.reservation.domain;

public record PaymentInfo(
        OrderId orderId,
        Amount totalAmount,
        PaymentKey paymentKey
) {
}
