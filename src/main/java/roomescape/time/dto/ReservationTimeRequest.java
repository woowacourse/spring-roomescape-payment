package roomescape.time.dto;

import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.time.entity.ReservationTime;

@Schema(description = "예약 시간 요청")
public record ReservationTimeRequest(
        @Schema(description = "예약 시간", example = "23:00") LocalTime startAt) {
    public ReservationTime toReservationTime() {
        return new ReservationTime(startAt);
    }
}
