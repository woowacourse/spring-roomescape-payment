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

    public Payment(Long id, String paymentKey, String order_id, Long amount) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.order_id = order_id;
        this.amount = amount;
    }

    public Payment(String paymentKey, String order_id, Long amount) {
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
