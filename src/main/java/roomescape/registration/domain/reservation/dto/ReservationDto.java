package roomescape.registration.domain.reservation.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.registration.domain.reservation.domain.Reservation;

public record ReservationDto(
        long id,
        String memberName,
        String themeName,
        LocalDate date,
        LocalTime startAt
) {
    public static ReservationDto from(Reservation reservation) {
        return new ReservationDto(
                reservation.getId(),
                reservation.getMember().getName(),
                reservation.getTheme().getName(), reservation.getDate(),
                reservation.getReservationTime().getStartAt()
        );
    }
}
