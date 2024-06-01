package roomescape.controller.dto;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import roomescape.domain.reservation.ReservationTime;

public record FindTimeResponse(Long id, @JsonFormat(pattern = "HH:mm") LocalTime startAt) {

    public static FindTimeResponse from(ReservationTime time) {
        return new FindTimeResponse(
                time.getId(),
                time.getStartAt()
        );
    }
}
