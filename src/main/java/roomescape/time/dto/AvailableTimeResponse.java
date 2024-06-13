package roomescape.time.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.time.domain.ReservationTime;

public record AvailableTimeResponse(
        TimeResponse time,
        @Schema(description = "예약 여부", example = "true")
        boolean alreadyBooked) {
    public static AvailableTimeResponse of(ReservationTime time, boolean alreadyBooked) {
        return new AvailableTimeResponse(TimeResponse.from(time), alreadyBooked);
    }
}
