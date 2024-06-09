package roomescape.controller.dto.response;

import roomescape.domain.reservation.Reservation;

public record MyReservationResponse(
        ReservationResponse reservation,
        long rank) {
    public static MyReservationResponse from(Reservation reservation, Long rank) {
        return new MyReservationResponse(
                ReservationResponse.from(reservation),
                rank
        );
    }
}
