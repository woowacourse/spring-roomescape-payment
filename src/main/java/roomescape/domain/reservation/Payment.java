package roomescape.domain.reservation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.util.Objects;
import roomescape.domain.exception.DomainValidationException;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String paymentKey;

    @Column(nullable = false, unique = true)
    private String orderId;

    @Column(nullable = false)
    private BigDecimal amount;

    protected Payment() {
    }

    public Payment(String paymentKey, String orderId, BigDecimal amount) {
        this(null, paymentKey, orderId, amount);
    }

    public Payment(Long id, String paymentKey, String orderId, BigDecimal amount) {
        validate(paymentKey, orderId, amount);

        this.id = id;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }

    private void validate(String paymentKey, String orderId, BigDecimal amount) {
        if (paymentKey == null || paymentKey.isBlank()) {
            throw new DomainValidationException("결제 키는 필수 값입니다.");
        }

        if (orderId == null || orderId.isBlank()) {
            throw new DomainValidationException("주문 ID는 필수 값입니다.");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainValidationException("금액은 0보다 커야 합니다.");
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Payment payment)) {
            return false;
        }

        return this.getId() != null && Objects.equals(getId(), payment.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
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

    public BigDecimal getAmount() {
        return amount;
    }
}
