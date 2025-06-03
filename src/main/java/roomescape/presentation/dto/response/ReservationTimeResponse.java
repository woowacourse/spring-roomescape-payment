package roomescape.presentation.dto.response;

import java.time.LocalTime;
import roomescape.business.model.entity.TimeSlot;

public record ReservationTimeResponse(
        String id,
        LocalTime startAt
) {
    public static ReservationTimeResponse from(TimeSlot timeSlot) {
        return new ReservationTimeResponse(
                timeSlot.getId().value(),
                timeSlot.getStartAt()
        );
    }
}
