package roomescape.domain.reservation.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;

@Entity
public class ReservationPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Reservation reservation;

    @OneToOne
    private Payment payment;

    public ReservationPayment(Reservation reservation, Payment payment) {
        this.reservation = reservation;
        this.payment = payment;
    }

    protected ReservationPayment() {
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
