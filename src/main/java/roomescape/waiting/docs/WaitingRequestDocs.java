package roomescape.waiting.docs;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.waiting.dto.request.WaitingRequest;
import java.time.LocalDate;

@Schema(description = "대기 요청")
public record WaitingRequestDocs(
        @Schema(description = "예약 날짜", example = "2024-03-20")
        LocalDate date,
        @Schema(description = "시간 ID", example = "1")
        Long timeId,
        @Schema(description = "테마 ID", example = "1")
        Long themeId) {

    public WaitingRequest toWaitingRequest() {
        return new WaitingRequest(date, timeId, themeId);
    }
} 