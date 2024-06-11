package roomescape.reservation.service.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationInfo(String themeName, LocalDate date, LocalTime time) {
    public static ReservationInfo from(roomescape.reservation.domain.ReservationInfo reservation) {
        return new ReservationInfo(
                reservation.getThemeName(),
                reservation.getDate(),
                reservation.getTimeValue()
        );
    }
}
