package roomescape.reservation.dto;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.reservation.model.ReservationTime;

public record AvailableReservationTimeResponse(
        Long timeId,
        @JsonFormat(pattern = "HH:mm")
        @Schema(type = "string", example = "14:30", pattern = "HH:mm")
        LocalTime startAt,
        @Schema(type = "boolean", example = "false")
        boolean alreadyBooked
) {
    public static AvailableReservationTimeResponse of(
            final ReservationTime reservationTime,
            final boolean alreadyBooked
    ) {
        return new AvailableReservationTimeResponse(
                reservationTime.getId(),
                reservationTime.getStartAt(),
                alreadyBooked);
    }
}
