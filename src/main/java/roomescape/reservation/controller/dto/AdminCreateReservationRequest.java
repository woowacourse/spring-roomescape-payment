package roomescape.reservation.controller.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record AdminCreateReservationRequest(
        @NotNull LocalDate date,
        @NotNull Long timeId,
        @NotNull Long themeId,
        @NotNull Long memberId
) {
}
