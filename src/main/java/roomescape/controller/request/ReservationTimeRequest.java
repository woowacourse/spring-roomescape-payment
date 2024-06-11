package roomescape.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record ReservationTimeRequest(
        @NotNull
        @Schema(description = "예약 시간", example = "10:00")
        LocalTime startAt) {
}
