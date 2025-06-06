package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    public static final int UNIT_PRICE_OF_RESERVATION = 1000;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String paymentKey;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private Long amount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected Payment() {
    }

    public Payment(String paymentKey, String orderId, Long amount, LocalDateTime createdAt) {
        validatePrivateKey(paymentKey);
        validateOrderId(orderId);
        validateAmount(amount);
        validateCreatedAt(createdAt);
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    private void validatePrivateKey(String privateKey) {
        if (privateKey == null || privateKey.isBlank()) {
            throw new IllegalArgumentException("키가 올바르지 않습니다.");
        }
    }

    private void validateOrderId(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("주문 번호가 올바르지 않습니다.");
        }
    }

    private void validateAmount(Long amount) {
        if (amount == null || amount < 0) {
            throw new IllegalArgumentException("결제 금액은 null이 될 수 없습니다.");
        }
        if (amount < UNIT_PRICE_OF_RESERVATION || amount % UNIT_PRICE_OF_RESERVATION != 0) {
            throw new IllegalArgumentException("결제 금액이 잘못되었습니다.");
        }
    }

    private void validateCreatedAt(LocalDateTime createdAt) {
        if (createdAt == null) {
            throw new IllegalArgumentException("결제 시각은 null이 될 수 없습니다.");
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
