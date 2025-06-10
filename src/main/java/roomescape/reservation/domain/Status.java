package roomescape.reservation.domain;

public enum Status {

    RESERVED("예약"),

    ;

    private final String message;

    Status(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
