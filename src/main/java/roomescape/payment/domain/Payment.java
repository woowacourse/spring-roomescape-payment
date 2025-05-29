package roomescape.payment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey;

    private Integer amount;

    protected Payment() {}

    public Payment(Long id, String paymentKey, Integer amount) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.amount = amount;
    }

    public Payment(String paymentKey, Integer amount) {
        this(null, paymentKey, amount);
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public Integer getAmount() {
        return amount;
    }
}
