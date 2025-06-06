package roomescape.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;

public record AvailableReservationTimeResponse(
        @Schema(example = "1")
        Long id,

        @JsonFormat(pattern = "HH:mm")
        LocalTime startAt,

        @Schema(example = "true")
        boolean isReserved
) {
}
