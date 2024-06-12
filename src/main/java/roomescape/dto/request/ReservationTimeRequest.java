package roomescape.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.ReservationTime;

import java.time.LocalTime;

public record ReservationTimeRequest(
        @Schema(description = "예약 시간") LocalTime startAt
) {
    public ReservationTime toReservationTime() {
        return new ReservationTime(startAt);
    }
}
