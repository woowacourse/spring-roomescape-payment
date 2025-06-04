package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.reservationitem.ReservationTime;

import java.time.LocalTime;

public record ReservationTimeWithAvailabilityResponse(
        @Schema(example = "1")
        long id,

        @Schema(example = "15:00")
        LocalTime startAt,
        
        @Schema(example = "true")
        boolean isBooked
) {

    public static ReservationTimeWithAvailabilityResponse from(ReservationTime time, boolean isBooked) {
        return new ReservationTimeWithAvailabilityResponse(time.getId(), time.getStartAt(), isBooked);
    }
}
