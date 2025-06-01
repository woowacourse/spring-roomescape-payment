package roomescape.payment.domain;

public class Payment {

    private final String paymentKey;

    private final String orderId;

    private final Integer amount;

    private final String paymentType;

    public Payment(final String paymentKey, final String orderId, final Integer amount, final String paymentType) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentType = paymentType;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public Integer getAmount() {
        return amount;
    }

    public String getPaymentType() {
        return paymentType;
    }
}
