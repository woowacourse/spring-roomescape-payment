package roomescape.waiting.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "대기 요청")
public record WaitingRequest(
        @Schema(description = "예약 날짜", example = "2024-03-20")
        LocalDate date,
        @Schema(description = "시간 ID", example = "1")
        Long timeId,
        @Schema(description = "테마 ID", example = "1")
        Long themeId) {
    public WaitingRequest {
        if (date == null) {
            throw new IllegalArgumentException("날짜는 null 일 수 없습니다.");
        }

        if (timeId == null) {
            throw new IllegalArgumentException("예약 시간 번호는 null 일 수 없습니다.");
        }

        if (themeId == null) {
            throw new IllegalArgumentException("테마 번호는 null 일 수 없습니다.");
        }
    }
}
