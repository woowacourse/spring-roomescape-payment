package roomescape.domain.payment;

public enum PaymentStatus {
    READY,
    IN_PROGRESS,
    WAITING_FOR_DEPOSIT,
    DONE,
    CANCELED,
    PARTIAL_CANCELED,
    ABORTED,
    EXPIRED,
    ;

    public boolean isNotDone() {
        return this != DONE;
    }
}
