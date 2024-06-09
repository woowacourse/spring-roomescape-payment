package roomescape.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Payed extends Payment {

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private long totalAmount;

    public Payed(String paymentKey, String orderId, long totalAmount) {
        super(State.DONE);
        validatePaymentKey(paymentKey);
        validateOrderId(orderId);
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
    }

    protected Payed() {

    }

    private void validatePaymentKey(String paymentKey) {
        if (paymentKey == null || paymentKey.isBlank()) {
            throw new IllegalArgumentException("PaymentKey 는 필수값입니다.");
        }
    }

    private void validateOrderId(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("orderId 는 필수값입니다.");
        }
    }

    @Override
    public String getPaymentKey() {
        return paymentKey;
    }

    @Override
    public String getOrderId() {
        return orderId;
    }

    @Override
    public Long getAmount() {
        return totalAmount;
    }
}
