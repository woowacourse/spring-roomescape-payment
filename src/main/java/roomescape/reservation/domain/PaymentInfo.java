package roomescape.reservation.domain;

public record PaymentInfo(
        OrderId orderId,
        Amount totalAmount,
        PaymentKey paymentKey
) {
    public String orderIdAsString() {
        return orderId.orderId();
    }

    public Long totalAmountAsLong() {
        return totalAmount.amount();
    }

    public String paymentKeyAsString() {
        return paymentKey.paymentKey();
    }
}
