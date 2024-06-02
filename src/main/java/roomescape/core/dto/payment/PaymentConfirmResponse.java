package roomescape.core.dto.payment;

import roomescape.core.domain.Payment;

public class PaymentConfirmResponse {
    private String paymentKey;
    private String orderId;
    private Long totalAmount;

    public PaymentConfirmResponse() {
    }

    public PaymentConfirmResponse(Payment payment) {
        this.paymentKey = payment.getPaymentKey();
        this.orderId = payment.getOrderId();
        this.totalAmount = payment.getAmount();
    }

    public Payment toPayment() {
        return new Payment(paymentKey, orderId, totalAmount);
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
