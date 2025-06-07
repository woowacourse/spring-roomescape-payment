package roomescape.reservation.domain;

public enum ReservationStatus {
    BOOKED("예약"),
    PAYMENT_PENDING("결제 대기")
    ;

    private final String displayName;

    ReservationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
