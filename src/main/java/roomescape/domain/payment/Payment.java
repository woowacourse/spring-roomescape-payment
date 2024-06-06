package roomescape.domain.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class Payment {

    @Column
    private String paymentKey;

    @Column(nullable = false)
    private Integer amount;

    public Payment(String paymentKey, Integer amount) {
        this.paymentKey = paymentKey;
        this.amount = amount;
    }

    protected Payment() {
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public Integer getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(paymentKey, payment.paymentKey) && Objects.equals(amount, payment.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentKey, amount);
    }
}
