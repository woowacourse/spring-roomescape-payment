package roomescape.reservation.domain;

import roomescape.payment.domain.Payment;

public class ReservationWithRankAndPayment {

    private Reservation reservation;
    private Long rank;
    private Payment payment;

    protected ReservationWithRankAndPayment() {
    }

    public ReservationWithRankAndPayment(Reservation reservation, long rank, Payment payment) {
        this.reservation = reservation;
        this.rank = rank;
        this.payment = payment;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public Long getRank() {
        return rank;
    }

    public Payment getPayment() {
        return payment;
    }
}
