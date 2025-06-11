package roomescape.presentation.dto.request;

import java.time.LocalDate;

public record ReservationRequest(
        String themeId,
        String timeSlotId,
        LocalDate date
) {
}
