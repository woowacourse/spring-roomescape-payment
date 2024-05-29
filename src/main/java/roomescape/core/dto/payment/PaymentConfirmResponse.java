package roomescape.core.dto.payment;

import roomescape.core.domain.Payment;

public class PaymentConfirmResponse {
    private Long totalAmount;
    private String orderId;
    private String paymentKey;

    public PaymentConfirmResponse(Payment payment) {
        this(payment.getAmount(), payment.getOrderId(), payment.getPaymentKey());
    }

    public PaymentConfirmResponse(Long totalAmount, String orderId, String paymentKey) {
        this.totalAmount = totalAmount;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
    }

    public PaymentConfirmResponse() {
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaymentKey() {
        return paymentKey;
    }
}
