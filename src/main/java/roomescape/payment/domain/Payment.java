package roomescape.payment.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Objects;

@Entity
public class Payment {

    @Id
    private PaymentKey paymentKey;

    @Embedded
    private Amount amount;

    public Payment(final String paymentKey, final Long amount) {
        this.paymentKey = new PaymentKey(paymentKey);
        this.amount = new Amount(amount);
    }

    protected Payment() {
    }

    public PaymentKey getPaymentKey() {
        return paymentKey;
    }

    public Amount getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
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
