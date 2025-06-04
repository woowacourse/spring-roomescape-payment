package roomescape.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record ReservationPendingRequest(
        @Schema(example = "2025-06-05")
        @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate date,

        @Schema(example = "1")
        long themeId,

        @Schema(example = "1")
        long timeId
) {
}
