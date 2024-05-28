package roomescape.domain.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey;

    private Long amount;

    private Boolean deleted;

    private LocalDateTime requestedAt;

    private LocalDateTime approvedAt;

    public Payment() {
    }

    public Payment(String paymentKey, Long amount, Boolean deleted, LocalDateTime requestedAt, LocalDateTime approvedAt) {
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.deleted = deleted;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
    }

    public Payment(String paymentKey, Long amount) {
        this.paymentKey = paymentKey;
        this.amount = amount;
    }

    public static Payment createEmpty() {
        return new Payment(null, 0L, null, null, null);
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public Long getAmount() {
        return amount;
    }
}
