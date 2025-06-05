package roomescape.global.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ErrorResponse(
        @Schema(example = "예외 타입")
        String errorType,
        @Schema(example = "예외 메시지")
        String message,
        @Schema(example = "2025-06-05T05:37:01")
        LocalDateTime timestamp
) {
    public static ErrorResponse of(Exception e) {
        return new ErrorResponse(
                e.getClass().getSimpleName(),
                e.getMessage(),
                LocalDateTime.now()
        );
    }
}
