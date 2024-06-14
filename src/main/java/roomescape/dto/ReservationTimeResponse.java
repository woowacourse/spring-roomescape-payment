package roomescape.dto;

import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.entity.ReservationTime;

@Schema(description = "예약 시간 응답 DTO 입니다.")
public record ReservationTimeResponse(
        @Schema(description = "예약 시간 ID 입니다.")
        long id,
        @Schema(description = "예약 시간입니다.")
        LocalTime startAt
) {
    public static ReservationTimeResponse from(ReservationTime reservationTime) {
        return new ReservationTimeResponse(reservationTime.getId(), reservationTime.getStartAt());
    }
}
