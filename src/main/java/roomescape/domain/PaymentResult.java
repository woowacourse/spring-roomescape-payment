package roomescape.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PaymentResult extends AuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String orderId;
    @Column(nullable = false)
    private String paymentKey;
    @Column(nullable = false)
    private String paymentType;
    @Column(nullable = false)
    private Long amount;

    protected PaymentResult() {
    }

    private PaymentResult(String orderId, String paymentKey, String paymentType, Long amount) {
        validate(orderId, paymentKey, paymentType, amount);
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.paymentType = paymentType;
        this.amount = amount;
    }

    public static PaymentResult createWithoutId(String orderId, String paymentKey, String paymentType, long amount) {
        return new PaymentResult(orderId, paymentKey, paymentType, amount);
    }

    private void validate(String orderId, String paymentKey, String paymentType, Long amount) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("주문 ID는 비어있을 수 없습니다.");
        }
        if (paymentKey == null || paymentKey.isBlank()) {
            throw new IllegalArgumentException("결제 키는 비어있을 수 없습니다.");
        }
        if (paymentType == null || paymentType.isBlank()) {
            throw new IllegalArgumentException("결제 타입은 비어있을 수 없습니다.");
        }
        if (amount <= 0L) {
            throw new IllegalArgumentException("결제 금액은 0원이하일 수 없습니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public Long getAmount() {
        return amount;
    }
}
