package roomescape.presentation.response;

import java.time.LocalDate;
import java.util.List;
import roomescape.domain.reservation.Reservation;

public record ReservationResponse(
        long id,
        UserResponse user,
        LocalDate date,
        TimeSlotResponse time,
        ThemeResponse theme
) {

    public static List<ReservationResponse> fromReservations(
            final List<Reservation> reservations
    ) {
        return reservations.stream()
                .map(ReservationResponse::fromReservation)
                .toList();
    }

    public static ReservationResponse fromReservation(
            final Reservation reservation
    ) {
        return new ReservationResponse(
                reservation.getId(),
                UserResponse.fromUser(reservation.getUser()),
                reservation.getDate(),
                TimeSlotResponse.fromTimeSlot(reservation.getTimeSlot()),
                ThemeResponse.fromTheme(reservation.getTheme())
        );
    }
}
