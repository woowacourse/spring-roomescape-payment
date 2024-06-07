package roomescape.domain.dto;

import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;

public class ReservationWithRank {

    private final Reservation reservation;
    private final long rank;
    private final Payment payment;

    public ReservationWithRank(Reservation reservation, long rank, Payment payment) {
        this.reservation = reservation;
        this.rank = rank;
        this.payment = payment;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public long getRank() {
        return rank;
    }

    public Payment getPayment() {
        return payment;
    }
}
