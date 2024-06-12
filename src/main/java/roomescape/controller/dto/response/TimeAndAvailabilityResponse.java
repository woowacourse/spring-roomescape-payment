package roomescape.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.reservation.ReservationTime;

public record TimeAndAvailabilityResponse(
        @Schema(description = "사간 정보")
        TimeResponse time,
        @Schema(description = "예약 여부", example = "true")
        boolean alreadyBooked
) {
    public static TimeAndAvailabilityResponse from(ReservationTime time, boolean alreadyBooked) {
        return new TimeAndAvailabilityResponse(
                new TimeResponse(time.getId(), time.getStartAt()), alreadyBooked
        );
    }
}
