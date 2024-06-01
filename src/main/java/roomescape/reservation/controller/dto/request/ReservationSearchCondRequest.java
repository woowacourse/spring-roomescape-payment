package roomescape.reservation.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import roomescape.common.validator.ValidDateSearchPeriod;

@ValidDateSearchPeriod
public record ReservationSearchCondRequest(
        @NotNull Long themeId,
        @NotNull Long memberId,
        @NotNull LocalDate dateFrom,
        @NotNull LocalDate dateTo
) {
}

