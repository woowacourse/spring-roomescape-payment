package roomescape.model;

public enum ReservationStatus {
    PAYMENT_WAITING, RESERVED;

    public boolean isReserved() {
        return this == RESERVED;
    }
}
