package roomescape.service.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.reservation.BookedMember;
import roomescape.domain.reservation.Reservation;

public record BookedReservationResponse(
        Long id,
        Long reservationId,
        String theme,
        LocalDate date,
        LocalTime startAt
) {
    public static BookedReservationResponse from(BookedMember bookedMember) {
        Reservation reservation = bookedMember.getReservation();
        return new BookedReservationResponse(
                bookedMember.getId(),
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt()
        );
    }
}
