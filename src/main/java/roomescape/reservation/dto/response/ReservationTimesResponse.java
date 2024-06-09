package roomescape.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "예약 시간 정보 목록 응답", description = "모든 예약 시간 조회 응답시 사용됩니다.")
public record ReservationTimesResponse(
        @Schema(description = "모든 시간 목록") List<ReservationTimeResponse> times
) {
}
