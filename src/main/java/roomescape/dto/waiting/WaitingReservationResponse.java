package roomescape.dto.waiting;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.reservation.Reservation;

public record WaitingReservationResponse(
        Long id,
        String name,
        String theme,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        @JsonFormat(pattern = "HH-mm") LocalTime startAt
) {

    public static WaitingReservationResponse from(Reservation reservation) {
        return new WaitingReservationResponse(
                reservation.getId(),
                reservation.getMember().getName(),
                reservation.getTheme().getThemeName(),
                reservation.getDate(),
                reservation.getTime().getStartAt()
        );
    }
}
