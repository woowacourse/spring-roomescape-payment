package roomescape.application.reservation.query.dto;

import roomescape.domain.reservation.ReservationTime;

import java.time.LocalTime;

public record ReservationTimeResult(
        Long id,
        LocalTime startAt
) {

    public static ReservationTimeResult from(final ReservationTime reservationTime) {
        return new ReservationTimeResult(reservationTime.getId(), reservationTime.getStartAt());
    }
}
