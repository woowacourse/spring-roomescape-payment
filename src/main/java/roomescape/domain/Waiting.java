package roomescape.domain;

import roomescape.entity.Reservation;

public class Waiting {
    private static final int VALUE_FOR_WAITING_OVER = 0;

    private final Reservation reservation;
    private final long rank;

    private Waiting(Reservation reservation, long rank) {
        this.reservation = reservation;
        this.rank = rank;
    }

    public static Waiting of(Reservation reservation, long rank) {
        if (rank == VALUE_FOR_WAITING_OVER) {
            reservation.confirm();
        }
        return new Waiting(reservation, rank);
    }

    public boolean remainsWaitingRank() {
        return rank > VALUE_FOR_WAITING_OVER;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public long getRank() {
        return rank;
    }
}
