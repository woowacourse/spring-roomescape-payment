package roomescape.reservation.service.dto;

import roomescape.reservation.domain.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationInfo(String themeName, LocalDate date, LocalTime time) {
    public static ReservationInfo from(Reservation reservation) {
        return new ReservationInfo(
                reservation.getThemeName(),
                reservation.getDate(),
                reservation.getTimeValue()
        );
    }
}
