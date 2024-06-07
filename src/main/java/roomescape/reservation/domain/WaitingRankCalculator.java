package roomescape.reservation.domain;

import java.util.List;

public class WaitingRankCalculator {

    private static final Long INITIAL_RANK = 1L;

    private final List<ReservationWaiting> reservationWaitings;

    public WaitingRankCalculator(List<ReservationWaiting> reservationWaitings) {
        this.reservationWaitings = reservationWaitings;
    }

    public Long calculateWaitingRank(ReservationWaiting reservationWaiting) {
        return reservationWaitings.stream()
                .filter(other -> other.compareTo(reservationWaiting) < 0)
                .count()
                + INITIAL_RANK;
    }
}

