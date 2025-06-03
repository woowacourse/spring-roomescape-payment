package roomescape.application.reservation.query.dto;

import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationWithStatusResult(
        Long reservationId,
        String themeName,
        LocalDate reservationDate,
        LocalTime reservationTime,
        ReservationStatus status
) {

    public static ReservationWithStatusResult from(final Reservation reservation) {
        return new ReservationWithStatusResult(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                reservation.getStatus()
        );
    }
}
