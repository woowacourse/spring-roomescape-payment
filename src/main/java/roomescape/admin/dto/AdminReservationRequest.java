package roomescape.admin.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리자 권한 예약 요청")
public record AdminReservationRequest(
        @Schema(description = "예약 날짜", example = "#{T(java.time.LocalDate).now()}")
        LocalDate date,
        @Schema(description = "예약 날짜 ID", example = "1")
        long timeId,
        @Schema(description = "예약 테마 ID", example = "1")
        long themeId,
        @Schema(description = "예약자 ID", example = "1")
        long memberId) {
}
