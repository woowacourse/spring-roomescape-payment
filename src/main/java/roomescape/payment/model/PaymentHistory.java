package roomescape.payment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import roomescape.reservation.model.Reservation;

@Entity
@SQLDelete(sql = "UPDATE payment_history SET payment_status = 'RESERVATION_CANCELED' WHERE id = ?")
@SQLRestriction(value = "payment_status <> 'RESERVATION_CANCELED'")
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderId;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Long totalAmount;

    @Column(nullable = false)
    private LocalDateTime approvedAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentHistoryStatus paymentStatus;

    @OneToOne(fetch = FetchType.LAZY)
    private Reservation reservation;

    protected PaymentHistory() {
    }

    public PaymentHistory(final String orderId, final String paymentKey, final String status, final Long totalAmount,
                          final LocalDateTime approvedAt, final Reservation reservation) {
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.approvedAt = approvedAt;
        this.status = status;
        this.totalAmount = totalAmount;
        this.paymentStatus = PaymentHistoryStatus.DONE;
        this.reservation = reservation;
    }

    public boolean hasSameReservation(final Reservation other) {
        return reservation.equals(other);
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

    public String getStatus() {
        return status;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public PaymentHistoryStatus getPaymentStatus() {
        return paymentStatus;
    }

    public Reservation getReservation() {
        return reservation;
    }
}
