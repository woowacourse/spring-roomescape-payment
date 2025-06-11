package roomescape.reservation.domain;

public enum ReservationStatus {
    BOOKED("예약"),
    WAITING("대기"),
    PAYMENT_PENDING("결제대기"),
    CANCELLED("취소"),
    REFUNDED("환불완료")
    ;

    private final String displayName;

    ReservationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
