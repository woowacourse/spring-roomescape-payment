package roomescape.dto.reservation;

import java.time.LocalDate;

public record ReservationFilter(
        Long member,
        Long theme,
        LocalDate dateFrom,
        LocalDate dateTo
) {
}
