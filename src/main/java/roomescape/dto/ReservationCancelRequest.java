package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "예약 취소 요청 DTO 입니다.")
public record ReservationCancelRequest(@Schema(description = "예약 취소 이유입니다.") String cancelReason) {
}
