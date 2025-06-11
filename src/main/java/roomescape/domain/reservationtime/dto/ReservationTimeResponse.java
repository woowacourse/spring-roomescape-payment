package roomescape.domain.reservationtime.dto;

import java.time.LocalTime;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.exception.custom.reason.ResponseInvalidException;

public record ReservationTimeResponse(
        Long id,
        LocalTime startAt
) {
    public ReservationTimeResponse {
        if (id == null || startAt == null) {
            throw new ResponseInvalidException();
        }
    }

    public static ReservationTimeResponse from(final ReservationTime reservationTime) {
        return new ReservationTimeResponse(
                reservationTime.getId(),
                reservationTime.getStartAt()
        );
    }
}
