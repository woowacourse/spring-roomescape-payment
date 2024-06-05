package roomescape.domain.reservation;

public enum ReservationStatus {
    RESERVED("예약"),
    WAITING("대기"),
    PAYMENT_PENDING("결제 대기"),
    CANCELED("취소");

    private final String value;

    ReservationStatus(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
