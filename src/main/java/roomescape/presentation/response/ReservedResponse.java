package roomescape.presentation.response;

import java.time.LocalDate;
import java.util.List;
import roomescape.domain.reservation.reserved.Reserved;

public record ReservedResponse(long id, UserResponse user, LocalDate date, TimeSlotResponse time, ThemeResponse theme) {

    public static List<ReservedResponse> fromReservations(final List<Reserved> reservations) {
        return reservations.stream().map(ReservedResponse::fromReservation).toList();
    }

    public static ReservedResponse fromReservation(final Reserved reservation) {
        return new ReservedResponse(reservation.getId(), UserResponse.fromUser(reservation.getUser()),
                reservation.getDate(), TimeSlotResponse.fromTimeSlot(reservation.getTimeSlot()),
                ThemeResponse.fromTheme(reservation.getTheme()));
    }
}
