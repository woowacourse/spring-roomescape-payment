package roomescape.domain.reservation.model;

public record ReservationWaitingWithOrder(ReservationWaiting reservationWaiting, int order) {

    private static final int FIRST_RESERVATION_WAITING_ORDER_VALUE = 1;

    public boolean isFirstOrder() {
        return this.order == FIRST_RESERVATION_WAITING_ORDER_VALUE;
    }
}
