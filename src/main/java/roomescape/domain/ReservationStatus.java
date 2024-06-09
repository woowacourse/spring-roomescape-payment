package roomescape.domain;

public enum ReservationStatus {

    BOOKING("예약"),
    WAITING("예약 대기"),
    PENDING("결제 대기")
    ;

    private final String message;

    ReservationStatus(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String createStatusMessage(Long rank) {
        return rank + "번째 " + message;
    }
}
