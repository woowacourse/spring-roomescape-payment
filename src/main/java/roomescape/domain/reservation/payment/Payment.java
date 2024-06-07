package roomescape.domain.reservation.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Long id;

    @Column(name = "orderId", nullable = false)
    String orderId;

    @Column(name = "amount", nullable = false)
    long amount;

    @Column(name = "payementKey", nullable = false)
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

    public long getId() {
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
}
