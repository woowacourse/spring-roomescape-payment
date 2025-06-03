package roomescape.payment.infrastructure.dto;

public class TossPaymentRequest {

    private final Integer amount;

    private final String orderId;

    private final String paymentKey;

    public TossPaymentRequest(final Integer amount, final String orderId, final String paymentKey) {
        this.amount = amount;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
    }

    public Integer getAmount() {
        return amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaymentKey() {
        return paymentKey;
    }
}
