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

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private PaymentStatus status;

    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Reservation reservation;

    public Payment(Long id, String orderId, String paymentKey, Long amount, PaymentStatus status, Reservation reservation) {
        this.id = id;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.status = status;
        this.reservation = reservation;
    }

    public Payment() {
    }

    public Payment(String orderId, String paymentKey, Long amount, PaymentStatus status, Reservation reservation) {
        this(null, orderId, paymentKey, amount, status, reservation);
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


    public Long getAmount() {
        return amount;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void confirm() {
        this.status = PaymentStatus.DONE;
    }

    public void cancel() {
        this.status = PaymentStatus.CANCEL;
    }

    public void removeReservation() {
        this.reservation = null;
    }
}
