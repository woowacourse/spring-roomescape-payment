package roomescape.exception.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "에러 응답")
public record ErrorResponse(
        @Schema(description = "에러 메시지", example = "유효하지 않은 입력값입니다")
        String message
) {
    public static ErrorResponse from(String message) {
        return new ErrorResponse(message);
    }
}

