package roomescape.waiting.docs;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.waiting.dto.response.WaitingResponse;

@Schema(description = "대기 응답")
public record WaitingResponseDocs(
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

    public static WaitingResponseDocs from(WaitingResponse response) {
        return new WaitingResponseDocs(
                response.id(),
                response.name(),
                response.theme(),
                response.date(),
                response.startAt()
        );
    }
} 