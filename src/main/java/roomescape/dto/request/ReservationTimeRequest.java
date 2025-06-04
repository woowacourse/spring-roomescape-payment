package roomescape.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

public record ReservationTimeRequest(
        @Schema(example = "15:00")
        @NotNull @DateTimeFormat(pattern = "HH:mm")
        LocalTime startAt
) {
}
