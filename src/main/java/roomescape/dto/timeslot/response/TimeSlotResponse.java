package roomescape.dto.timeslot.response;

import java.time.LocalTime;
import roomescape.domain.timeslot.TimeSlot;

public record TimeSlotResponse(Long id, LocalTime startAt) {
    public static TimeSlotResponse from(final TimeSlot findTimeSlot) {
        return new TimeSlotResponse(findTimeSlot.id(), findTimeSlot.startAt());
    }
}
