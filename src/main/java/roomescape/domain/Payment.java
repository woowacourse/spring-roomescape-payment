package roomescape.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String paymentKey;
    @Column(nullable = false)
    private String orderId;
    @Column(nullable = false)
    private BigDecimal amount;

    protected Payment() {

    }

    public Payment(Long id, String paymentKey, String orderId, BigDecimal amount) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }

    public Payment(String paymentKey, String orderId, BigDecimal amount) {
        this(null, paymentKey, orderId, amount);
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

    public BigDecimal getAmount() {
        return amount;
    }
}
