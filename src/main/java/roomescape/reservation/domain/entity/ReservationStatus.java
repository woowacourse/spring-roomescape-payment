package roomescape.reservation.domain.entity;

import java.util.List;

public enum ReservationStatus {

    CONFIRMATION("예약"),
    WAITING("예약대기"),
    PENDING("결제대기");

    private final String statusName;

    ReservationStatus(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }

    public boolean isNotWaiting() {
        return !this.equals(WAITING);
    }

    public boolean isConfirmation() {
        return this.equals(CONFIRMATION);
    }

    public boolean isPending() {
        return this.equals(PENDING);
    }

    public static List<ReservationStatus> getConfirmationStatuses() {
        return List.of(CONFIRMATION, PENDING);
    }
}
