package roomescape.web.controller.request;

import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record SearchCondition(
        @Positive Long memberId,
        @Positive Long themeId,
        LocalDate dateFrom,
        LocalDate dateTo
) {
}
