package roomescape.payment.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import roomescape.reservation.domain.Reservation;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderId;

    @Column(nullable = false, unique = true)
    private String paymentKey;

    private LocalDateTime paymentDateTime;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private PaymentStatus status;

    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Reservation reservation;

    public Payment(String orderId, String paymentKey, LocalDateTime paymentDateTime, Long amount, PaymentStatus status, Reservation reservation) {
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.paymentDateTime = paymentDateTime;
        this.amount = amount;
        this.status = status;
        this.reservation = reservation;
    }

    public Payment() {
    }

    public void cancel() {
        this.status = PaymentStatus.CANCEL;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public LocalDateTime getPaymentDateTime() {
        return paymentDateTime;
    }

    public Long getAmount() {
        return amount;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public PaymentStatus getStatus() {
        return status;
    }
}
