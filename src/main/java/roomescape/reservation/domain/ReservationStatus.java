package roomescape.reservation.domain;

public enum ReservationStatus {

    REQUESTED, CONFIRMED, FAILED;

    public boolean isFinished() {
        return this != REQUESTED;
    }

    public boolean isConfirmed() {
        return this != CONFIRMED;
    }
}
