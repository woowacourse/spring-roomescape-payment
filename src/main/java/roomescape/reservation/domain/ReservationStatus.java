package roomescape.reservation.domain;

public enum ReservationStatus {
    CONFIRMED("예약 확정"),
    CANCELED("예약 취소");

    private final String title;

    ReservationStatus(final String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
