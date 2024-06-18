package roomescape.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import roomescape.reservation.domain.ReservationTime;

@Schema(name = "예약 시간 정보", description = "예약 시간 추가 및 조회 응답시 사용됩니다.")
public record ReservationTimeResponse(
        @Schema(description = "예약 시간 번호. 예약 시간을 식별할 때 사용합니다.")
        Long id,
        @Schema(description = "예약 시간", type = "string", example = "09:00")
        LocalTime startAt
) {

    public static ReservationTimeResponse from(ReservationTime reservationTime) {
        return new ReservationTimeResponse(reservationTime.getId(), reservationTime.getStartAt());
    }
}
