package roomescape.core.dto.payment;

import roomescape.core.domain.Payment;

public class PaymentResponse {
    private final Long id;
    private final String paymentKey;
    private final String orderId;
    private final Long totalAmount;

    public PaymentResponse(Payment payment) {
        this.id = payment.getId();
        this.paymentKey = payment.getPaymentKey();
        this.orderId = payment.getOrderId();
        this.totalAmount = payment.getAmount();
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }
}
