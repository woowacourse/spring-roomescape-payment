package roomescape.payment;

public class Payment {

    private final String paymentKey;
    private final String orderId;
    private final int amount;

    public Payment(String paymentKey, String orderId, int amount) {
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

    public int getAmount() {
        return amount;
    }
}
