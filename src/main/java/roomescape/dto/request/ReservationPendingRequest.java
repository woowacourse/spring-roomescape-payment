package roomescape.dto.request;

import java.time.LocalDate;

public record ReservationPendingRequest(
        LocalDate date,
        long themeId,
        long timeId
) {
}
