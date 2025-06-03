package roomescape.presentation.dto.response;

import java.time.LocalDate;
import roomescape.business.model.entity.Reservation;

public record ReservationResponse(
        String id,
        UserResponse user,
        LocalDate date,
        ReservationTimeResponse time,
        ThemeResponse theme
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId().value(),
                UserResponse.from(reservation.getMember()),
                reservation.getDate().value(),
                ReservationTimeResponse.from(reservation.getTimeSlot()),
                ThemeResponse.from(reservation.getTheme())
        );
    }
}
