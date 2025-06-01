package roomescape.domain.payment;

public class Payment {

    private final String paymentKey;
    private final String orderId;
    private final Long totalAmount;

    public Payment(final String paymentKey, final String orderId, final Long totalAmount) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }
}
