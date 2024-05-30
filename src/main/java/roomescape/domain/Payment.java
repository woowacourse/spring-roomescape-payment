package roomescape.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import roomescape.exception.ExceptionType;
import roomescape.exception.RoomescapeException;

@Entity
public class Payment {

    private static final String ORDER_ID_PREFIX = "WTEST";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey;

    private String orderId;

    private Long amount;

    protected Payment() {
    }

    public Payment(Long id, String paymentKey, String orderId, Long amount) {
        validatePaymentKey(paymentKey);
        validateOrderId(orderId);
        validateAmount(amount);

        this.id = id;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }

    private void validatePaymentKey(String paymentKey) {
        if (paymentKey == null || paymentKey.isBlank()) {
            throw new RoomescapeException(ExceptionType.EMPTY_PAYMENT_KEY);
        }
    }

    private void validateOrderId(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new RoomescapeException(ExceptionType.EMPTY_ORDER_ID);
        }
        if (!orderId.startsWith(ORDER_ID_PREFIX)) {
            throw new RoomescapeException(ExceptionType.INVALID_ORDER_ID);
        }
    }

    private void validateAmount(Long amount) {
        if (amount == null) {
            throw new RoomescapeException(ExceptionType.EMPTY_AMOUNT);
        }
        if (amount < 0) {
            throw new RoomescapeException(ExceptionType.INVALID_AMOUNT);
        }
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

    public Long getAmount() {
        return amount;
    }
}
