package roomescape.domain;

import roomescape.entity.Payment;
import roomescape.entity.Reservation;

public class ReservationDetail {
    Reservation reservation;
    Payment payment;
    long rank;

    public ReservationDetail(Reservation reservation, Payment payment, long rank) {
        this.reservation = reservation;
        this.payment = payment;
        this.rank = rank;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public Payment getPayment() {
        return payment;
    }

    public long getRank() {
        return rank;
    }
}
