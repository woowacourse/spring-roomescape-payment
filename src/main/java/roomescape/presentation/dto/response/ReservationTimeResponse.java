package roomescape.presentation.dto.response;

import java.time.LocalTime;
import roomescape.business.model.entity.ReservationTime;

public record ReservationTimeResponse(
        String id,
        LocalTime startAt
) {
    public static ReservationTimeResponse from(ReservationTime reservationTime) {
        return new ReservationTimeResponse(
                reservationTime.getId().value(),
                reservationTime.getStartTime().value()
        );
    }
}
