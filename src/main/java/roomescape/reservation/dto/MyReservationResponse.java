package roomescape.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.domain.Reservation;

public record MyReservationResponse(
        Long reservationId,
        Long ownerId,
        String themeName,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,
        @JsonFormat(pattern = "HH:mm")
        LocalTime startAt
) {

    public static MyReservationResponse from(Reservation reservation) {
        return new MyReservationResponse(
                reservation.getId(),
                reservation.getMemberId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt()
        );
    }
}
