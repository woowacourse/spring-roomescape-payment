package roomescape.dto.reservation;

import java.time.LocalDate;

import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.dto.theme.ReservedThemeResponse;

public record ReservationResponse(
        Long id,
        String name,
        LocalDate date,
        ReservationTimeResponse time,
        ReservedThemeResponse theme,
        ReservationStatus status
) {
    public ReservationResponse(final Reservation reservation) {
        this(
                reservation.getId(),
                reservation.getMember().getNameString(),
                reservation.getDate(),
                ReservationTimeResponse.from(reservation.getTime()),
                ReservedThemeResponse.from(reservation.getTheme()),
                reservation.getStatus()
        );
    }
}
