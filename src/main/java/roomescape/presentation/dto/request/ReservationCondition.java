package roomescape.presentation.dto.request;

import java.time.LocalDate;

public record ReservationCondition(
        String themeId,
        String userId,
        LocalDate dateFrom,
        LocalDate dateTo
) {
}
