package roomescape.reservation.domain;

public enum ReservationStatus {
    BOOKED,
    CANCELLED,
    ;

    public boolean isBooked() {
        return this == BOOKED;
    }
}
