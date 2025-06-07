package roomescape.reservation.domain;

public enum ReservationStatus {
    PENDING("결제 대기"),
    PAID("예약");

    private final String status;

    ReservationStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
