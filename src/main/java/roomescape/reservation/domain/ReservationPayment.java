package roomescape.reservation.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class ReservationPayment {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    protected ReservationPayment() {
    }

    public ReservationPayment(Reservation reservation, Payment payment) {
        this.reservation = reservation;
        this.payment = payment;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public Payment getPayment() {
        return payment;
    }
}
