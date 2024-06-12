package roomescape.domain.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String paymentKey;
    private Long amount;
    private String orderId;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;

    public Payment() {
    }

    public Payment(String paymentKey, Long amount, String orderId, String requestedAt, String approvedAt) {
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.orderId = orderId;
        this.requestedAt = LocalDateTime.parse(requestedAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        this.approvedAt = LocalDateTime.parse(approvedAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
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
