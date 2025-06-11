package roomescape.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Schema(description = "에러 응답 DTO")
public record ErrorResponse(
        @Schema(description = "에러 발생 시간", example = "2024-03-20T14:00:00")
        LocalDateTime timestamp,

        @Schema(description = "HTTP 상태 코드", example = "400")
        int status,

        @Schema(description = "에러 메시지", example = "잘못된 요청입니다.")
        String message
) {

    public static ErrorResponse of(HttpStatus status, String message) {
        LocalDateTime timestamp = LocalDateTime.now();
        return new ErrorResponse(timestamp, status.value(), message);
    }
}
