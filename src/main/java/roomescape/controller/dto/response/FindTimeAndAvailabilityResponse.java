package roomescape.controller.dto.response;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record FindTimeAndAvailabilityResponse(
        Long id,
        @JsonFormat(pattern = "HH:mm") LocalTime startAt,
        boolean alreadyBooked
) {
}
