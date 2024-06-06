package roomescape.domain.reservationwaiting;

public class ReservationWaitingWithRank {
    private final ReservationWaiting reservationWaiting;
    private final Long rank;

    public ReservationWaitingWithRank(ReservationWaiting reservationWaiting, Long rank) {
        this.reservationWaiting = reservationWaiting;
        this.rank = rank;
    }

    public ReservationWaiting getReservationWaiting() {
        return reservationWaiting;
    }

    public Long getRank() {
        return rank;
    }
}
