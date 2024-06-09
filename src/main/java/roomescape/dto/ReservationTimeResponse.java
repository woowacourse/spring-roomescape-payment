package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import roomescape.domain.ReservationTime;

@Schema(description = "예약 시간 응답 DTO")
public record ReservationTimeResponse(@Schema(description = "id", example = "1") long id,
                                      @Schema(description = "시작 시간", pattern = "hh:MM", example = "11:30") LocalTime startAt) {
    public static ReservationTimeResponse from(ReservationTime reservationTime) {
        return new ReservationTimeResponse(reservationTime.getId(), reservationTime.getStartAt());
    }
}
