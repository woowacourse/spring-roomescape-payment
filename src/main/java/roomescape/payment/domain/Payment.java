package roomescape.payment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey;
    private String orderId;
    private Long amount;

    @Enumerated(value = EnumType.STRING)
    private PaymentGateway paymentGateway;

    protected Payment() {
    }

    public Payment(final Long id, final String paymentKey, final String orderId,
        final Long amount, final PaymentGateway paymentGateway) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentGateway = paymentGateway;
    }

    public Payment(
        final String paymentKey,
        final String orderId,
        final Long amount,
        final PaymentGateway paymentGateway
    ) {
        this(null, paymentKey, orderId, amount, paymentGateway);
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

    public PaymentGateway getPaymentGateway() {
        return paymentGateway;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Payment payment = (Payment) o;
        if (id == null || payment.id == null) {
            return false;
        }
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
