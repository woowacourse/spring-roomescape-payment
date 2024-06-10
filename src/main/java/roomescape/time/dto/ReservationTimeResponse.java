package roomescape.time.dto;

import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.time.entity.ReservationTime;

@Schema(description = "예약 시간 응답")
public record ReservationTimeResponse(
        @Schema(description = "예약 시간 ID", defaultValue = "1") long id,
        @Schema(description = "예약 시간", defaultValue = "23:00") LocalTime startAt) {
    public static ReservationTimeResponse from(ReservationTime reservationTime) {
        return new ReservationTimeResponse(reservationTime.getId(), reservationTime.getStartAt());
    }
}
