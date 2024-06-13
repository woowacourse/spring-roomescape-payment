package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;

public record ReservationTimeRequest(
        @Schema(description = "시작 시간")
        LocalTime startAt
) {
}
