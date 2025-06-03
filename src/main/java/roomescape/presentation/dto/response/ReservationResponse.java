package roomescape.presentation.dto.response;

import java.time.LocalDate;
import roomescape.business.model.entity.Reservation;

public record ReservationResponse(
        String id,
        MemberResponse user,
        LocalDate date,
        TimeSlotResponse time,
        ThemeResponse theme
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId().value(),
                MemberResponse.from(reservation.getMember()),
                reservation.getDate().value(),
                TimeSlotResponse.from(reservation.getTimeSlot()),
                ThemeResponse.from(reservation.getTheme())
        );
    }
}
