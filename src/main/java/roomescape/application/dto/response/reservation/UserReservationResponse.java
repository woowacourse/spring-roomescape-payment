package roomescape.application.dto.response.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.reservation.ReservationWithRank;

public record UserReservationResponse(
        Long reservationId,
        String theme,
        LocalDate date,
        LocalTime time,
        String status,
        Long rank
) {

    public static UserReservationResponse from(ReservationWithRank reservation) {
        return new UserReservationResponse(
                reservation.reservationId(),
                reservation.theme(),
                reservation.date(),
                reservation.time(),
                reservation.status(),
                reservation.waitingRank());
    }
}
