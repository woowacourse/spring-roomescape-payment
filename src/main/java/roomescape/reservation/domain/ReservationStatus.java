package roomescape.reservation.domain;

import roomescape.global.exception.ViolationException;

import java.util.Arrays;

public enum ReservationStatus {
    BOOKING("BOOKING", "예약"),
    WAITING("WAITING", "%d번째 예약대기");

    private final String identifier;
    private final String description;

    ReservationStatus(String identifier, String description) {
        this.identifier = identifier;
        this.description = description;
    }

    public static ReservationStatus from(String identifier) {
        return Arrays.stream(ReservationStatus.values())
                .filter(status -> status.identifier.equals(identifier))
                .findAny()
                .orElseThrow(() -> new ViolationException("예약 상태 식별자가 올바르지 않습니다."));
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

    public String getDescription() {
        return description;
    }
}
