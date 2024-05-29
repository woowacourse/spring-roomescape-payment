package roomescape.domain.reservation;

public record ReservationWithRank(Reservation reservation, Long rank) {
    public boolean isReserved() {
        return reservation.isReserved();
    }

    public boolean isWaiting() {
        return reservation.isWaiting();
    }
}
