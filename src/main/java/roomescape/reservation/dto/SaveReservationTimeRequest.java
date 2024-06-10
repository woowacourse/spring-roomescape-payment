package roomescape.reservation.dto;

import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.reservation.model.ReservationTime;

public record SaveReservationTimeRequest(
        @Schema(type = "string", example = "14:30", pattern = "HH:mm")
        LocalTime startAt
) {
    public ReservationTime toReservationTime() {
        return new ReservationTime(startAt);
    }
}
