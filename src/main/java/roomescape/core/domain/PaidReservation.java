package roomescape.core.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class PaidReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    public PaidReservation() {
    }

    public PaidReservation(final Reservation reservation, final Payment payment) {
        this(null, reservation, payment);
    }

    public PaidReservation(final Long id, final Reservation reservation, final Payment payment) {
        this.id = id;
        this.reservation = reservation;
        this.payment = payment;
    }

    public Long getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public Payment getPayment() {
        return payment;
    }
}
