package roomescape.reservation.dto;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.reservation.model.ReservationTime;

public record ReservationTimeResponse(Long id,
                                      @JsonFormat(pattern = "HH:mm")
                                      @Schema(type = "string", example = "14:30", pattern = "HH:mm")
                                      LocalTime startAt) {
    public static ReservationTimeResponse from(final ReservationTime reservationTime) {
        return new ReservationTimeResponse(reservationTime.getId(), reservationTime.getStartAt());
    }
}

