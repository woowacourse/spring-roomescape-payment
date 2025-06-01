package roomescape.dto.response;

import roomescape.domain.reservationitem.ReservationTime;

import java.time.LocalTime;

public record ReservationTimeResponse(
        long id,
        LocalTime startAt
) {

    public static ReservationTimeResponse from(ReservationTime reservationTime) {
        return new ReservationTimeResponse(reservationTime.getId(), reservationTime.getStartAt());
    }
}
