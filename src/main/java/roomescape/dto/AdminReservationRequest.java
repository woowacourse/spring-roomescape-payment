package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "관리자 예약 조회 요청 DTO 입니다.")
public record AdminReservationRequest(
        @Schema(description = "예약 날짜입니다.")
        LocalDate date,
        @Schema(description = "예약 시간 ID 입니다.")
        long timeId,
        @Schema(description = "테마 ID 입니다.")
        long themeId,
        @Schema(description = "사용자 ID 입니다.")
        long memberId) {
}
