package roomescape.infrastructure.payment.toss;

public enum PaymentStatus {
    READY,
    IN_PROGRESS,
    WAITING_FOR_DEPOSIT,
    DONE,
    CANCELED,
    PARTIAL_CANCELED,
    ABORTED,
    EXPIRED;

    public static boolean isAcceptedStatus(String status) {
        return status.equalsIgnoreCase(READY.name());
    }

}
