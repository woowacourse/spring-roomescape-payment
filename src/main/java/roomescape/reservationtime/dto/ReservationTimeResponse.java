package roomescape.reservationtime.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;

@Schema(description = "예약 시간 응답")
public record ReservationTimeResponse(

        @Schema(description = "예약 시간 ID", example = "1")
        long id,

        @Schema(description = "시작 시간", example = "14:00")
        LocalTime startAt) {
}
