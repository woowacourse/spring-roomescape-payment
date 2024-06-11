package roomescape.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;

public record IsReservedTimeResponse(
        @Schema(description = "예약 시간 ID", example = "1")
        long timeId,
        @Schema(description = "예약 시간", example = "10:00")
        LocalTime startAt,
        @Schema(description = "예약 여부", example = "true")
        boolean alreadyBooked) {
}
