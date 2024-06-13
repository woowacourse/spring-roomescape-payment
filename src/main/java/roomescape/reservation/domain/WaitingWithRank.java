package roomescape.reservation.domain;

public class WaitingWithRank {

    private Waiting waiting;
    private Long rank;

    protected WaitingWithRank() {
    }

    public WaitingWithRank(Waiting waiting, Long rank) {
        this.waiting = waiting;
        this.rank = rank;
    }

    public Waiting getWaiting() {
        return waiting;
    }

    public Long getRank() {
        return rank;
    }
}
