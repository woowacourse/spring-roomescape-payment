package roomescape.presentation.dto.response;

import java.time.LocalTime;
import roomescape.business.model.entity.TimeSlot;

public record TimeSlotResponse(
        String id,
        LocalTime startAt
) {
    public static TimeSlotResponse from(TimeSlot timeSlot) {
        return new TimeSlotResponse(
                timeSlot.getId().value(),
                timeSlot.getStartAt()
        );
    }
}
