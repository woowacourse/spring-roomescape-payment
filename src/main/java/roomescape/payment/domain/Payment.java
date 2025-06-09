package roomescape.payment.domain;

import jakarta.persistence.*;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_key", nullable = false)
    private String paymentKey;

    @Column(name = "amount", nullable = false)
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
