package roomescape.mvc.reservation.response;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.mvc.reservation.domain.Reservation;

public record FindAllReservationResponse(
        Long id,
        LocalDate date,
        String memberName,
        String themeName,
        LocalTime startAt
) {

    public FindAllReservationResponse(Reservation reservation) {
        this(
                reservation.getId(),
                reservation.getDate(),
                reservation.getMember().getName(),
                reservation.getTheme().getName(),
                reservation.getReservationTime().getStartAt()
        );
    }
}
