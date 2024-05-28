package roomescape.core.dto.payment;

public class PaymentConfirmResponse {
    private Integer totalAmount;
    private String orderId;
    private String paymentKey;

    public PaymentConfirmResponse(Integer totalAmount, String orderId, String paymentKey) {
        this.totalAmount = totalAmount;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
    }

    public PaymentConfirmResponse() {}

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaymentKey() {
        return paymentKey;
    }
}
