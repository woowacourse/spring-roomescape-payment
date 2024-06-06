package roomescape.payment.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;

    private String paymentKey;

    private Long totalAmount;

    @Column(name = "payment_method")
    private String method;

    private LocalDateTime requestedAt;

    private LocalDateTime approvedAt;

    public Payment() {
    }

    public Payment(String orderId, String paymentKey, Long totalAmount, String method, LocalDateTime requestedAt, LocalDateTime approvedAt) {
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.totalAmount = totalAmount;
        this.method = method;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
    }

    public Payment(String orderId,
                   String paymentKey,
                   Long totalAmount,
                   String method,
                   String requestedAt,
                   String approvedAt) {
        this(orderId,
                paymentKey,
                totalAmount,
                method,
                LocalDateTime.parse(requestedAt, DateTimeFormatter.ISO_DATE_TIME),
                LocalDateTime.parse(approvedAt, DateTimeFormatter.ISO_DATE_TIME));
    }

    public Long getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public String getMethod() {
        return method;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }
}
