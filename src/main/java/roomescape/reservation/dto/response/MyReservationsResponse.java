package roomescape.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "회원의 예약 및 대기 목록 조회 응답", description = "회원의 예약 및 대기 목록 조회 응답시 사용됩니다.")
public record MyReservationsResponse(
        @Schema(description = "현재 로그인한 회원의 예약 및 대기 목록") List<MyReservationResponse> myReservationResponses
) {
}
