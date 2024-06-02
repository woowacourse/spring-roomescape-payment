package roomescape.reservation.domain.entity;

import java.util.List;

public enum ReservationStatus {

    CONFIRMED("예약"),
    CANCELLATION_WAITING("예약대기"),
    PAYMENT_REQUIRED("결제대기");

    private final String statusName;

    ReservationStatus(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }

    public boolean isNotWaiting() {
        return !this.equals(CANCELLATION_WAITING);
    }

    public boolean isConfirmed() {
        return this.equals(CONFIRMED);
    }

    public boolean isPaymentRequired() {
        return this.equals(PAYMENT_REQUIRED);
    }

    public static List<ReservationStatus> getConfirmationStatuses() {
        return List.of(CONFIRMED, PAYMENT_REQUIRED);
    }
}
