package roomescape.domain.reservationwaiting;

public class ReservationWaitingWithRank {
    private final ReservationWaiting waiting;
    private final long rank;

    public ReservationWaitingWithRank(ReservationWaiting waiting, long rank) {
        this.waiting = waiting;
        this.rank = rank;
    }

    public ReservationWaiting getWaiting() {
        return waiting;
    }

    public long getRank() {
        return rank;
    }
}
