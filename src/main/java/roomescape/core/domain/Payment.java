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

    @Column(nullable = false, unique = true)
    private String paymentKey;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private Long amount;

    public Payment() {
    }

    public Payment(String paymentKey, String orderId, Long amount) {
        this(null, paymentKey, orderId, amount);
    }

    public Payment(final Long id, final String paymentKey,
                   final String orderId, final Long amount) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
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

    public Long getAmount() {
        return amount;
    }
}
