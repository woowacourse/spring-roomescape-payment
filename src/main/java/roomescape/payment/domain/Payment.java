package roomescape.payment.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private PaymentKey paymentKey;

    @Embedded
    private OrderId orderId;

    @Embedded
    private Amount amount;

    public Payment(final String paymentKey, final String orderId, final Long amount) {
        this.paymentKey = new PaymentKey(paymentKey);
        this.orderId = new OrderId(orderId);
        this.amount = new Amount(amount);
    }

    protected Payment() {
    }

    public Long getId() {
        return id;
    }

    public PaymentKey getPaymentKey() {
        return paymentKey;
    }

    public Amount getAmount() {
        return amount;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    @Override
    public final boolean equals(final Object o) {
        if (!(o instanceof final Payment payment)) {
            return false;
        }

        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
