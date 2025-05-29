package roomescape.presentation.api.reservation.response;

import roomescape.application.reservation.query.dto.ReservationTimeResult;

public record ReservationTimeResponse(
        Long id,
        String startAt
) {

    public static ReservationTimeResponse from(final ReservationTimeResult reservationTimeResult) {
        return new ReservationTimeResponse(
                reservationTimeResult.id(),
                ReservationDateTimeFormat.TIME.format(reservationTimeResult.startAt())
        );
    }
}
