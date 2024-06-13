package roomescape.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long reservationId;
    @Column(nullable = false)
    private String paymentKey;
    @Column(nullable = false)
    private String orderName;
    @Column(nullable = false)
    private String requestedAt;
    @Column(nullable = false)
    private String approvedAt;
    @Column(nullable = false)
    private String currency;
    @Column(nullable = false)
    private long totalAmount;

    protected Payment() {
    }

    public Payment(Long reservationId, String paymentKey, String orderName, String requestedAt, String approvedAt, String currency, long totalAmount) {
        this(null, reservationId, paymentKey, orderName, requestedAt, approvedAt, currency, totalAmount);
    }

    public Payment(Long id, Long reservationId, String paymentKey, String orderName, String requestedAt, String approvedAt, String currency, long totalAmount) {
        this.id = id;
        this.reservationId = reservationId;
        this.paymentKey = paymentKey;
        this.orderName = orderName;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.currency = currency;
        this.totalAmount = totalAmount;
    }

    public Long getId() {
        return id;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderName() {
        return orderName;
    }

    public String getRequestedAt() {
        return requestedAt;
    }

    public String getApprovedAt() {
        return approvedAt;
    }

    public String getCurrency() {
        return currency;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
