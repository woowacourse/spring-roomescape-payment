package roomescape.controller.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import roomescape.domain.reservation.Reservation;

public record CreateReservationResponse(
        Long id,
        String memberName,
        LocalDate date,
        @JsonFormat(pattern = "HH:mm") LocalTime time,
        String themeName) {

    public static CreateReservationResponse from(Reservation reservation) {
        return new CreateReservationResponse(
                reservation.getId(),
                reservation.getMember().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                reservation.getTheme().getName()
        );
    }
}
