package roomescape.domain.reservation;

import roomescape.domain.payment.ReservationPayment;

public class ReservationWithPayment {
    private final Reservation reservation;
    private final ReservationPayment reservationPayment;

    public ReservationWithPayment(Reservation reservation, ReservationPayment reservationPayment) {
        this.reservation = reservation;
        this.reservationPayment = reservationPayment;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public ReservationPayment getReservationPayment() {
        return reservationPayment;
    }
}
