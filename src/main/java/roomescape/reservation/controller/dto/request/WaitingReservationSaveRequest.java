package roomescape.reservation.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@NotNull
public record WaitingReservationSaveRequest(
        LocalDate date,
        Long themeId,
        Long timeId
) {
}
