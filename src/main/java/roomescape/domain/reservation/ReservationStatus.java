package roomescape.domain.reservation;

public enum ReservationStatus {
    RESERVATION("예약"),
    PAYMENT_WAITING("결제대기"),
    ;

    private final String value;

    ReservationStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean isPaymentWaiting() {
        return this == PAYMENT_WAITING;
    }
}
