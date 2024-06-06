package roomescape.dto.request.reservation;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record ReservationCriteriaRequest(
        Long themeId,
        Long memberId,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo
) {
}
