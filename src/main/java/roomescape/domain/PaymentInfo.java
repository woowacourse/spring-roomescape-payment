package roomescape.domain;

public class PaymentInfo {

    private final int amount;
    private final String orderId;
    private final String paymentKey;

    public PaymentInfo(int amount, String orderId, String paymentKey) {
        this.amount = amount;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
    }

    public int getAmount() {
        return amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaymentKey() {
        return paymentKey;
    }
}
