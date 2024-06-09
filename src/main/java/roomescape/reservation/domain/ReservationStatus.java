package roomescape.reservation.domain;

public enum ReservationStatus {
    WAITING_FOR_PAYMENT(false),
    DONE_PAYMENT(true),
    ADMIN_RESERVE(true),
    ;

    private final boolean isPaid;

    ReservationStatus(boolean isPaid) {
        this.isPaid = isPaid;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public boolean isNeedRefund() {
        return this == DONE_PAYMENT;
    }
}
