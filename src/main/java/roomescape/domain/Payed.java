package roomescape.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import roomescape.exception.PaymentException;
import roomescape.exception.RoomescapeException;
import roomescape.exception.RoomescapeExceptionType;

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

    private void validatePaymentKey(String paymentKey) {
        if(paymentKey == null || paymentKey.isBlank()) {
            throw new IllegalArgumentException("PaymentKey 는 필수값입니다.");
        }
    }

    private void validateOrderId(String orderId) {
        if(orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("orderId 는 필수값입니다.");
        }
    }

    protected Payed() {

    }
}
