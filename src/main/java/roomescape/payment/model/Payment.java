package roomescape.payment.model;

public class Payment {
    private String paymentKey;
    private String orderId;
    private Long amount;

    public Payment(final String paymentKey, final String orderId, final Long amount) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public Long getAmount() {
        return amount;
    }
}
