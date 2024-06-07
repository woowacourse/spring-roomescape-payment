package roomescape.controller.dto;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import roomescape.domain.reservation.ReservationTime;

public record CreateTimeResponse(Long id,
                                 @JsonFormat(pattern = "HH:mm") LocalTime startAt) {

    public static CreateTimeResponse from(ReservationTime reservationTime) {
        return new CreateTimeResponse(reservationTime.getId(), reservationTime.getStartAt());
    }
}