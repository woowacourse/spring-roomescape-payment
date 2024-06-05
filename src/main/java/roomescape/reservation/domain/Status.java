package roomescape.reservation.domain;

public enum Status {
    SUCCESS("예약"),
    CANCEL("취소"),
    WAIT("대기"),
    PAYMENT_PENDING("결제 대기")
    ;

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    public boolean isSuccess() {
        return this == SUCCESS;
    }

    public boolean isWait() {
        return this == WAIT;
    }

    public boolean isPaymentPending() {
        return this == PAYMENT_PENDING;
    }

    public String getDisplayName() {
        return displayName;
    }
}
