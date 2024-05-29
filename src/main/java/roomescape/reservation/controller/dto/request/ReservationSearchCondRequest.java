package roomescape.reservation.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import roomescape.common.validator.ValidDateRange;

@ValidDateRange
@NotNull
public record ReservationSearchCondRequest(
        long themeId,
        long memberId,
        LocalDate dateFrom,
        LocalDate dateTo
) {
}

