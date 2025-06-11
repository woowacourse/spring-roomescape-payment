package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import roomescape.domain.reservationitem.ReservationTime;

@Schema(description = "예약 시간 정보 응답 객체")
public record ReservationTimeResponse(
        @Schema(description = "시간 ID", example = "1")
        long id,

        @Schema(description = "예약 시작 시간", example = "14:30")
        LocalTime startAt
) {

    public static ReservationTimeResponse from(ReservationTime reservationTime) {
        return new ReservationTimeResponse(reservationTime.getId(), reservationTime.getStartAt());
    }
}
