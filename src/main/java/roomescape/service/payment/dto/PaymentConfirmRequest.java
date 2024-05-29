package roomescape.service.payment.dto;

public class PaymentConfirmRequest {
    private final String orderId;
    private final Integer amount;
    private final String paymentKey;

    public PaymentConfirmRequest(String orderId, Integer amount, String paymentKey) {
        this.orderId = orderId;
        this.amount = amount;
        this.paymentKey = paymentKey;
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
}
