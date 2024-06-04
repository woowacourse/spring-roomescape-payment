package roomescape.domain.reservation;

public enum ReservationStatus {
    RESERVED("예약"),
    WAITING("대기");

    private final String value;

    ReservationStatus(final String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
