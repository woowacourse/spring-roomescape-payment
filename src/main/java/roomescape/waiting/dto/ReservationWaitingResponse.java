package roomescape.waiting.dto;

import java.time.LocalDate;
import roomescape.theme.dto.ReservationThemeResponse;
import roomescape.time.dto.ReservationTimeResponse;
import roomescape.waiting.domain.ReservationWaiting;

public record ReservationWaitingResponse(
        long id,
        LocalDate date,
        ReservationTimeResponse time,
        ReservationThemeResponse theme
) {
    public static ReservationWaitingResponse from(final ReservationWaiting reservationWaiting) {
        return new ReservationWaitingResponse(
                reservationWaiting.getId(),
                reservationWaiting.getDate(),
                ReservationTimeResponse.from(reservationWaiting.getTime()),
                ReservationThemeResponse.from(reservationWaiting.getTheme())
        );
    }
}
