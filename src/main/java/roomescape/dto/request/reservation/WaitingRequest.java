package roomescape.dto.request.reservation;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record WaitingRequest(
        @NotNull LocalDate date,
        long timeId,
        long themeId
) {
}
