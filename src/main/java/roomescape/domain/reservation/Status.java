package roomescape.domain.reservation;

public enum Status {

    RESERVED("예약"),
    ;

    private final String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
