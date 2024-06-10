package roomescape.domain;

import jakarta.persistence.*;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private String order_id;

    @Column(nullable = false)
    private Long amount;

    protected Payment() {
    }

    public Payment(final Long id, final String paymentKey, final String order_id, final Long amount) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.order_id = order_id;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrder_id() {
        return order_id;
    }

    public Long getAmount() {
        return amount;
    }
}
