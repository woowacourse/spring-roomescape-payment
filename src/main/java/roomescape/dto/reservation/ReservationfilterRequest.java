package roomescape.dto.reservation;

import java.time.LocalDate;

public record ReservationfilterRequest(
        Long themeId,
        Long memberId,
        LocalDate startDate,
        LocalDate endDate
) {
}
