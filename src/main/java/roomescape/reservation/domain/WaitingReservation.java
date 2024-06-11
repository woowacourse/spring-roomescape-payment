package roomescape.reservation.domain;

import roomescape.global.exception.ViolationException;

public class WaitingReservation {
    private static final int MAX_RESERVATION_NUMBER_IN_TIME_SLOT = 1;

    private final Reservation reservation;
    private final long previousCount;

    public WaitingReservation(Reservation reservation, long previousCount) {
        if (reservation.isBooking()) {
            throw new ViolationException("예약이 대기 중이지 않습니다.");
        }

        this.reservation = reservation;
        this.previousCount = previousCount;
    }

    public String getStatusDescription() {
        return String.format(reservation.getStatusDescription(), calculateOrder());
    }

    private long calculateOrder() {
        return previousCount + MAX_RESERVATION_NUMBER_IN_TIME_SLOT;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public long getPreviousCount() {
        return previousCount;
    }
}
