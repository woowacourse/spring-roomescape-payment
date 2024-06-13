package roomescape.domain.reservation;

public enum Status {
    RESERVED("예약"),
    PAYMENT_WAITING("결제대기"),
    WAITING("예약대기"),
    CANCELED("예약취소")
    ;

    private final String value;

    Status(String value) {
        this.value = value;
    }

    public boolean isWaiting() {
        return this == WAITING;
    }

    public boolean isReserved() {
        return this == RESERVED;
    }

    public String getValue() {
        return value;
    }
}
