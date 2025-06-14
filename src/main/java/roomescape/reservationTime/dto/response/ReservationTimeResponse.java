package roomescape.reservationTime.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.reservationTime.domain.ReservationTime;

import java.time.LocalTime;

@Schema(description = "예약 시간 응답")
public record ReservationTimeResponse(
        @Schema(description = "시간 ID", example = "1")
        Long id,
        @Schema(description = "시작 시간", example = "10:00")
        LocalTime startAt) {
    public static ReservationTimeResponse from(final ReservationTime findReservationTime) {
        return new ReservationTimeResponse(findReservationTime.getId(), findReservationTime.getStartAt());
    }
}
