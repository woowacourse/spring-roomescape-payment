package roomescape.reservation.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record WaitingReservationSaveRequest(
        @NotNull LocalDate date,
        @NotNull Long themeId,
        @NotNull Long timeId
) {
}
