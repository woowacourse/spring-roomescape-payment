package roomescape.reservationtime.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;

@Schema(description = "예약 시간 요청")
public record ReservationTimeRequest(

        @Schema(description = "시작 시간", example = "14:00")
        LocalTime startAt) {
}
