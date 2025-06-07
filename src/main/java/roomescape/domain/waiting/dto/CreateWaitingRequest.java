package roomescape.domain.waiting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "대기열 생성 요청 DTO")
public record CreateWaitingRequest(
        @Schema(description = "날짜", example = "2026-06-07")
        LocalDate date,

        @Schema(description = "테마 ID", example = "1")
        Long theme,

        @Schema(description = "예약 시작 시간 ID", example = "1")
        Long time
) {
}
