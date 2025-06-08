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
    private long id;

    @Embedded
    private PaymentKey paymentKey;

    @Embedded
    private Amount amount;

    public Payment(final String paymentKey, final Long amount) {
        this.paymentKey = new PaymentKey(paymentKey);
        this.amount = new Amount(amount);
    }

    protected Payment() {
    }

    public long getId() {
        return id;
    }

    public PaymentKey getPaymentKey() {
        return paymentKey;
    }

    public Amount getAmount() {
        return amount;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Payment payment)) {
            return false;
        }
        return Objects.equals(paymentKey, payment.paymentKey);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(paymentKey);
    }
}
