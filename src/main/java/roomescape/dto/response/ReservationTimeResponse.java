package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.reservationitem.ReservationTime;

import java.time.LocalTime;

public record ReservationTimeResponse(
        @Schema(example = "1")
        long id,

        @Schema(example = "15:00")
        LocalTime startAt
) {

    public static ReservationTimeResponse from(ReservationTime reservationTime) {
        return new ReservationTimeResponse(reservationTime.getId(), reservationTime.getStartAt());
    }
}
