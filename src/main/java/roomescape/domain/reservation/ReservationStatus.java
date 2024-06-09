package roomescape.domain.reservation;

public enum ReservationStatus {
    ACCEPTED, CANCELED;

    public boolean isAccepted() {
        return this == ACCEPTED;
    }

    public boolean isCanceled() {
        return this == CANCELED;
    }
}
