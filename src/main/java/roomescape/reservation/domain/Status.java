package roomescape.reservation.domain;

public enum Status {

    RESERVED("예약"),
    WAITING("예약대기"),
    PENDING("결제대기");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
