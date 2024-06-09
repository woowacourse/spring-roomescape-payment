package roomescape.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "예약 목록 조회 응답", description = "모든 예약 정보 조회 응답시 사용됩니다.")
public record ReservationsResponse(
        @Schema(description = "모든 예약 및 대기 목록") List<ReservationResponse> reservations
) {
}
