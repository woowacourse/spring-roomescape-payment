package roomescape.reservation.domain;

import roomescape.global.exception.NotFoundException;

import java.util.Arrays;

public enum ReservationStatus {
    BOOKING("BOOKING"),
    WAITING("WAITING"),
    PENDING_PAYMENT("PENDING PAYMENT");

    private final String identifier;

    ReservationStatus(String identifier) {
        this.identifier = identifier;
    }

    public static ReservationStatus from(String identifier) {
        return Arrays.stream(ReservationStatus.values())
                .filter(status -> status.identifier.equals(identifier))
                .findAny()
                .orElseThrow(() -> new NotFoundException(identifier + "가 식별자인 reservation status가 없습니다.."));
    }

    public boolean isBooking() {
        return this == BOOKING;
    }

    public boolean isWaiting() {
        return this == WAITING;
    }

    public String getIdentifier() {
        return identifier;
    }
}
