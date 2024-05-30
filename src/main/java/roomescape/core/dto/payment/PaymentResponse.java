package roomescape.core.dto.payment;

import roomescape.core.domain.Payment;

public class PaymentResponse {

    private Long id;
    private String paymentKey;
    private Long amount;
    private String orderId;

    public PaymentResponse(Long id, String paymentKey, Long amount, String orderId) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.orderId = orderId;
    }

    public PaymentResponse(Payment payment) {
        this(payment.getId(), payment.getPaymentKey(), payment.getAmount(), payment.getOrderId());
    }

    public Payment toPayment() {
        return new Payment(id, paymentKey, amount, orderId);
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public Long getAmount() {
        return amount;
    }

    public String getOrderId() {
        return orderId;
    }
}
