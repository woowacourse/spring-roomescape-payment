package roomescape.service.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.reservation.BookedMember;
import roomescape.domain.reservation.Reservation;

public record UserBookedReservationResponse(
        Long id,
        Long reservationId,
        String theme,
        LocalDate date,
        LocalTime startAt
) {
    public static UserBookedReservationResponse from(BookedMember bookedMember) {
        Reservation reservation = bookedMember.getReservation();
        return new UserBookedReservationResponse(
                bookedMember.getId(),
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt()
        );
    }
}
