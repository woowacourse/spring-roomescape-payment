package roomescape.domain.reservation;

import java.util.List;

public enum ReservationStatus {
    RESERVATION("예약"),
    PAYMENT_WAITING("결제대기"),
    CANCELED("예약 취소")
    ;

    private final String value;

    ReservationStatus(String value) {
        this.value = value;
    }

    public static List<ReservationStatus> getActiveStatuses() {
        return List.of(RESERVATION, PAYMENT_WAITING);
    }

    public String getValue() {
        return value;
    }

    public boolean isReserved() {
        return this == RESERVATION;
    }

    public boolean isNotCanceled() {
        return this != CANCELED;
    }

    public boolean isPaymentWaiting() {
        return this == PAYMENT_WAITING;
    }
}
