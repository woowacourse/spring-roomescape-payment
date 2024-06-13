package roomescape.reservation.domain;

import roomescape.payment.domain.Payment;

public class ReservationWithPayment {

    private Reservation reservation;
    private Payment payment;

    public ReservationWithPayment() {
    }

    public ReservationWithPayment(Reservation reservation, Payment payment) {
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
