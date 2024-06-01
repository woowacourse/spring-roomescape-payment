package roomescape.payment.domain;

public enum CancelReason {

    CHANGE_MIND("단순 변심");

    private String name;

    CancelReason(String name) {
        this.name = name;
    }
}
