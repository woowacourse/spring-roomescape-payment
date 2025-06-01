package roomescape.domain.reservation;

public enum ReservationStatus {
    ACCEPTED("예약 확정"),
    NOT_PAID("결제 대기"),
    PENDING("예약 대기"),
    DENIED("예약 거절"),
    ;

    public final String description;

    ReservationStatus(String description) {
        this.description = description;
    }
}
