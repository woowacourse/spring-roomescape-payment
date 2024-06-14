package roomescape.domain;

public enum ReservationStatus {
    BOOKED, WAITING, WAITING_FOR_PAYMENT;

    public boolean isWaitingForPayment() {
        return this == WAITING_FOR_PAYMENT;
    }
}
