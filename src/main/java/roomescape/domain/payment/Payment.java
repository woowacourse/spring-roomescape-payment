package roomescape.domain.payment;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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
    private Long amount;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    protected Payment() {
    }

    public Payment(final String paymentKey, final String orderId, final Long amount) {
        this(null, paymentKey, orderId, amount);
    }

    public Payment(
            final Long id,
            final String paymentKey,
            final String orderId,
            final Long amount
    ) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
    }

    public void confirm() {
        status = PaymentStatus.CONFIRMED;
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

    public PaymentStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof Payment other
                && Objects.equals(getId(), other.getId())
                && Objects.equals(getPaymentKey(), other.getPaymentKey())
                && Objects.equals(getOrderId(), other.getOrderId())
                && Objects.equals(getAmount(), other.getAmount());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, paymentKey, orderId, amount);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", paymentKey='" + paymentKey + '\'' +
                ", orderId='" + orderId + '\'' +
                ", amount=" + amount +
                '}';
    }
}
