package roomescape.payment.domain;

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
    private String paymentKey;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private Long amount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    protected Payment() {
    }

    public Payment(String paymentKey, String orderId, Long amount, Reservation reservation) {
        this(null, paymentKey, orderId, amount, reservation);
    }

    public Payment(Long id, String paymentKey, String orderId, Long amount, Reservation reservation) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.reservation = reservation;
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

    public Reservation getReservation() {
        return reservation;
    }
}
