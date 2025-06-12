package roomescape.dto.timeslot.response;

import java.time.LocalTime;

public record TimeSlotConditionResponse(Long id, LocalTime startAt, boolean alreadyBooked) {
}
