package roomescape.domain.payment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import roomescape.domain.reservation.model.Reservation;

import java.time.LocalDateTime;

@Entity
public class PaymentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(length = 100, nullable = false)
    private String orderName;

    @Column(nullable = false)
    private Long totalAmount;

    @Column(nullable = false)
    private LocalDateTime approvedAt;

    @Column(length = 100, nullable = false)
    private String paymentKey;

    @Column(length = 100, nullable = false)
    private String paymentProvider;

    @OneToOne
    private Reservation reservation;

    protected PaymentHistory() {
    }

    public PaymentHistory(
            final PaymentStatus paymentStatus,
            final String orderName,
            final Long totalAmount,
            final LocalDateTime approvedAt,
            final String paymentKey,
            final String paymentProvider,
            final Reservation reservation
    ) {
        this.paymentStatus = paymentStatus;
        this.orderName = orderName;
        this.totalAmount = totalAmount;
        this.approvedAt = approvedAt;
        this.paymentKey = paymentKey;
        this.paymentProvider = paymentProvider;
        this.reservation = reservation;
    }

    public void cancelPayment() {
        this.paymentStatus = PaymentStatus.CANCELED;
    }

    public Long getId() {
        return id;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public String getOrderName() {
        return orderName;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getPaymentProvider() {
        return paymentProvider;
    }

    public Reservation getReservation() {
        return reservation;
    }
}
