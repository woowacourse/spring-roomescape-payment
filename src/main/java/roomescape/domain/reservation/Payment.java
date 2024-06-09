package roomescape.domain.reservation;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Table(name = "payment")
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Long id;

    @NotNull
    @Column(name = "order_id", unique = true)
    String orderId;

    @NotNull
    @Column(name = "amount")
    long amount;

    @NotNull
    @Column(name = "payment_key")
    String paymentKey;

    public Payment(String orderId, long amount, String paymentKey) {
        this(null, orderId, amount, paymentKey);
    }

    public Payment(Long id, String orderId, long amount, String paymentKey) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentKey = paymentKey;
    }

    protected Payment() {
    }

    public Long getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public Long getAmount() {
        return amount;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;

        if (id == null || ((Payment) o).id == null) {
            return Objects.equals(orderId, payment.orderId);
        }
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        if (id == null) {
            return Objects.hash(orderId);
        }
        return Objects.hash(id);
    }
}
