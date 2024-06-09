package roomescape.domain.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private int amount;

    protected Payment() {
    }

    public Payment(String paymentKey, int amount) {
        this(null, paymentKey, amount);
    }

    public Payment(Long id, String paymentKey, int amount) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public int getAmount() {
        return amount;
    }
}
