package roomescape.timeslot.dto.response;

import java.time.LocalTime;
import roomescape.timeslot.domain.TimeSlot;

public record TimeSlotResponse(Long id, LocalTime startAt) {
    public static TimeSlotResponse from(final TimeSlot findTimeSlot) {
        return new TimeSlotResponse(findTimeSlot.getId(), findTimeSlot.getStartAt());
    }
}
