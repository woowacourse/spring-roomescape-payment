package roomescape.domain.payment;

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
    private Long amount;
    private String orderId;
    private String requestedAt;
    private String approvedAt;

    public Payment() {
    }

    public Payment(String paymentKey, Long amount, String orderId, String requestedAt, String approvedAt) {
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.orderId = orderId;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
    }

    public Long getId() {
        return id;
    }

    public long getAmount() {
        return amount;
    }

    public String getPaymentKey() {
        return paymentKey;
    }
}
