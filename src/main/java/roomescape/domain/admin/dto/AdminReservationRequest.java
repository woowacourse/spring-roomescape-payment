package roomescape.domain.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "관리자 예약 요청 DTO")
public record AdminReservationRequest(
        @Schema(description = "예약 날짜", example = "2026-06-07")
        LocalDate date,

        @Schema(description = "테마 ID", example = "1")
        Long themeId,

        @Schema(description = "타임 ID", example = "1")
        Long timeId,

        @Schema(description = "회원 ID", example = "1")
        Long memberId
) {
}

