package roomescape.application.dto.response.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.reservation.Reservation;
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
                reservation.reservation().getId(),
                reservation.reservation().getDetail().getTheme().getName(),
                reservation.reservation().getDetail().getDate(),
                reservation.reservation().getDetail().getTime().getStartAt(),
                reservation.reservation().getStatus().name(),
                reservation.rank());
    }
}
