package roomescape.reservation.domain;

import roomescape.reservation.payment.domain.Payment;

public class ReservationWithPayment {

    private final Reservation reservation;
    private final Payment payment;

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
