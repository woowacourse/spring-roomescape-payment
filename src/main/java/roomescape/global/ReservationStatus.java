package roomescape.global;

public enum ReservationStatus {
    RESERVED("예약"),
    WAIT("%d 번째 예약대기"),
    PENDING("결제 대기"),
    CANCELED("예약 취소"),
    ;

    private final String text;

    ReservationStatus(final String text) {
        this.text = text;
    }

    public String renderText(long rank) {
        return this.text.formatted(rank);
    }
}
