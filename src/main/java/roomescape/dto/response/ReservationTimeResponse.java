package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.ReservationTime;

import java.time.LocalTime;

public record ReservationTimeResponse(
        @Schema(description = "예약 시간 엔티티 식별자") long id,
        @Schema(description = "예약 시간") LocalTime startAt
) {
    public static ReservationTimeResponse from(ReservationTime reservationTime) {
        return new ReservationTimeResponse(reservationTime.getId(), reservationTime.getStartAt());
    }
}
