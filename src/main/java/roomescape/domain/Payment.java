package roomescape.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

import java.util.Objects;

@Entity
public class Payment {

    @Transient
    public static final Payment DEFAULT_PAYMENT = new Payment(0L, "default_order_id", "default_payment_key",
            "default_order_name", 0L);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;
    @Column(nullable = false)
    private final String orderId;
    @Column(nullable = false)
    private final String paymentKey;
    @Column(nullable = false)
    private final String orderName;
    @Column(nullable = false)
    private final Long totalAmount;

    protected Payment() {
        this.id = null;
        this.paymentKey = null;
        this.orderId = null;
        this.orderName = null;
        this.totalAmount = null;
    }

    public Payment(final Long id, final String orderId, final String paymentKey,
                   final String orderName, final Long totalAmount) {
        this.id = id;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.orderName = orderName;
        this.totalAmount = totalAmount;
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

    public String getOrderName() {
        return orderName;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Payment payment = (Payment) o;
        return Objects.equals(getId(), payment.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
