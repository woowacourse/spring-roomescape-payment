package roomescape.waiting.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.waiting.domain.Waiting;

@Schema(description = "대기 응답")
public record WaitingResponse(
        @Schema(description = "대기 ID", example = "1")
        Long id,
        @Schema(description = "회원 이름", example = "홍길동")
        String name,
        @Schema(description = "테마명", example = "기본 테마")
        String theme,
        @Schema(description = "예약 날짜", example = "2024-03-20")
        String date,
        @Schema(description = "예약 시간", example = "10:00")
        String startAt) {
    public static WaitingResponse from(Waiting save) {
        return new WaitingResponse(
                save.getId(),
                save.getMember().getName(),
                save.getTheme().getName(),
                save.getDate().toString(),
                save.getTime().getStartAt().toString()
        );
    }
}
