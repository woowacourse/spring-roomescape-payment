package roomescape.domain.reservation.response;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.reservation.domain.Reservation;

public record FindReservationsByFilter(
        Long id,
        LocalDate date,
        String memberName,
        String themeName,
        LocalTime startAt
) {

    public FindReservationsByFilter(Reservation reservation) {
        this(
                reservation.getId(),
                reservation.getDate(),
                reservation.getMember().getName(),
                reservation.getTheme().getName(),
                reservation.getReservationTime().getStartAt()
        );
    }
}
