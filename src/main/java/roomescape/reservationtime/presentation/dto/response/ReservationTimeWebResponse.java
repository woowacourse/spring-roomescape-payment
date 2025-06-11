package roomescape.reservationtime.presentation.dto.response;

import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.reservationtime.domain.ReservationTime;

public record ReservationTimeWebResponse(
        @Schema(description = "예약시간 엔티티의 기본 키") Long id,
        @Schema(description = "예약 가능한 시간") LocalTime startAt
) {
    public static ReservationTimeWebResponse from(final ReservationTime reservationTime) {
        return new ReservationTimeWebResponse(reservationTime.getId(), reservationTime.getStartAt());
    }
}
