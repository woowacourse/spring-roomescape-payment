package roomescape.reservation.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import roomescape.common.validator.ValidDateRange;

@ValidDateRange
public record ReservationSearchCondRequest(
        @NotNull long themeId,
        @NotNull long memberId,
        @NotNull LocalDate dateFrom,
        @NotNull LocalDate dateTo
) {
}

