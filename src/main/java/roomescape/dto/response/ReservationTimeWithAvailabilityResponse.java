package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import roomescape.domain.reservationitem.ReservationTime;

@Schema(description = "예약 가능 여부를 포함한 시간 정보 응답 객체")
public record ReservationTimeWithAvailabilityResponse(
        @Schema(description = "시간 ID", example = "1")
        long id,

        @Schema(description = "예약 시작 시간", example = "14:30")
        LocalTime startAt,

        @Schema(description = "예약 여부 (true: 예약됨, false: 예약 가능)", example = "true")
        boolean isBooked
) {

    public static ReservationTimeWithAvailabilityResponse from(ReservationTime time, boolean isBooked) {
        return new ReservationTimeWithAvailabilityResponse(time.getId(), time.getStartAt(), isBooked);
    }
}
