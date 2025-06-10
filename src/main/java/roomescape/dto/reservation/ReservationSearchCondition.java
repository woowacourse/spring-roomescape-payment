package roomescape.dto.reservation;

import java.time.LocalDate;

public record ReservationSearchCondition(Long memberId, Long themeId, LocalDate dateFrom, LocalDate dateTo) {
}
