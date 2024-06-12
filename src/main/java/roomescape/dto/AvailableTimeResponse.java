package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import roomescape.domain.ReservationTime;

public record AvailableTimeResponse(
        @Schema(description = "시간 ID")
        long id,

        @Schema(description = "시작 시간")
        LocalTime startAt,

        @Schema(description = "예약 가능 여부")
        boolean isBooked
) {
    public static AvailableTimeResponse of(ReservationTime reservationTime, boolean isBooked) {
        return new AvailableTimeResponse(reservationTime.getId(), reservationTime.getStartAt(), isBooked);
    }
}
