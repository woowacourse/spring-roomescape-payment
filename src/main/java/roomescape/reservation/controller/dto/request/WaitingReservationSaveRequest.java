package roomescape.reservation.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@NotNull
public record WaitingReservationSaveRequest(
        LocalDate date,
        long themeId,
        long timeId
) {
}
