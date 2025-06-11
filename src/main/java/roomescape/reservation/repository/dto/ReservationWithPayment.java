package roomescape.reservation.repository.dto;

import java.util.Optional;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;

public class ReservationWithPayment {

    private Reservation reservation;
    private Payment payment;

    public ReservationWithPayment(Reservation reservation, Payment payment) {
        this.reservation = reservation;
        this.payment = payment;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public Optional<Payment> getPayment() {
        return Optional.ofNullable(payment);
    }
}
