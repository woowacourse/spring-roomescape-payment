package roomescape.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "예약 시간 정보 목록 응답", description = "특정 테마, 날짜에 대한 모든 예약 가능 시간 정보를 저장합니다.")
public record ReservationTimeInfosResponse(
        @Schema(description = "특정 테마, 날짜에 대한 예약 가능 여부를 포함한 시간 목록") List<ReservationTimeInfoResponse> reservationTimes
) {
}
