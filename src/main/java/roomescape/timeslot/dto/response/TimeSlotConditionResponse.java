package roomescape.timeslot.dto.response;

import java.time.LocalTime;

public record TimeSlotConditionResponse(Long id, LocalTime startAt, boolean alreadyBooked) {
}
