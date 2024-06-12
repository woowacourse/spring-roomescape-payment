package roomescape.reservation.domain;

public enum ReservationStatus {
    USER_RESERVE,
    ADMIN_RESERVE,
    ;

    public boolean isUserReserved() {
        return this == USER_RESERVE;
    }

    public boolean isAdminReserved() {
        return this == ADMIN_RESERVE;
    }
}
