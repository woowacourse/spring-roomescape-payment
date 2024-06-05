package roomescape.reservation.domain;

public class WaitingWithRank {

    private final Reservation waiting;
    private final Long rank;

    public WaitingWithRank(Reservation waiting, Long rank) {
        this.waiting = waiting;
        this.rank = rank;
    }

    public WaitingWithRank(Reservation waiting, Integer rank) {
        this(waiting, (long) rank);
    }

    public Reservation getWaiting() {
        return waiting;
    }

    public Long getRank() {
        return rank;
    }
}
