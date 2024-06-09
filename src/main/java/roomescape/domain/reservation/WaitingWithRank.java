package roomescape.domain.reservation;

public class WaitingWithRank {

    private Waiting waiting;
    private Long rank;

    public WaitingWithRank(Waiting waiting, Long rank) {
        this.waiting = waiting;
        this.rank = rank;
    }

    public Long getWaitingId() {
        return waiting.getId();
    }

    public ReservationSlot getReservationSlot() {
        return waiting.getReservation().getReservationSlot();
    }

    public Long getRank() {
        return rank + 1;
    }

    public Reservation getReservation() {
        return waiting.getReservation();
    }
}
