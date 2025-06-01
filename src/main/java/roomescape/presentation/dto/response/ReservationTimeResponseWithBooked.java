package roomescape.presentation.dto.response;

import java.time.LocalTime;
import roomescape.business.model.entity.ReservationTime;

public record ReservationTimeResponseWithBooked(
        String id,
        LocalTime startAt,
        boolean alreadyBooked
) {
    public static ReservationTimeResponseWithBooked of(ReservationTime time, boolean alreadyBooked) {
        return new ReservationTimeResponseWithBooked(time.getId().value(), time.getStartTime().value(), alreadyBooked);
    }
}
