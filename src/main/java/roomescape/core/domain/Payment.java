package roomescape.core.domain;

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

    @Column(unique = true, nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private Long amount;

    @Column(unique = true, nullable = false)
    private String orderId;

    public Payment(Long id, String paymentKey, Long amount, String orderId) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.orderId = orderId;
    }

    public Payment(String paymentKey, Long amount, String orderId) {
        this(null, paymentKey, amount, orderId);
    }

    public Payment() {
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public Long getAmount() {
        return amount;
    }

    public String getOrderId() {
        return orderId;
    }
}
