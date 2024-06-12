package roomescape.reservation.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Payment {
    @Id
    @GeneratedValue
    private Long id;

    private String paymentKey;

    private long amount;

    protected Payment() {
    }

    public Payment(String paymentKey, long amount) {
        this.id = null;
        this.paymentKey = paymentKey;
        this.amount = amount;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public long getAmount() {
        return amount;
    }
}
