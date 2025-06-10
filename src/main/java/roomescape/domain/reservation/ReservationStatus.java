package roomescape.domain.reservation;

public enum ReservationStatus {
    CONFIRMED("예약"),
    WAITING("대기"),
    CANCELED("취소"),
    PENDING("보류");

    private final String description;

    ReservationStatus(final String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
