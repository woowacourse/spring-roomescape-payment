package roomescape.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;

@Schema(name = "특정 테마, 날짜에 대한 시간 정보 응답", description = "특정 날짜와 테마에 대해, 예약 가능 여부를 포함한 시간 정보를 저장합니다.")
public record ReservationTimeInfoResponse(
        @Schema(description = "예약 시간 번호. 예약 시간을 식별할 때 사용합니다.")
        Long timeId,
        @Schema(description = "예약 시간", type = "string", example = "09:00")
        LocalTime startAt,
        @Schema(description = "이미 예약이 완료된 시간인지 여부")
        boolean alreadyBooked
) {
}
