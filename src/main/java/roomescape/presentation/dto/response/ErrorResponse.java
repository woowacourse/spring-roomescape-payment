package roomescape.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime timestamp,

        @Schema(example = "예외 상태")
        int status,

        @Schema(example = "예외 메시지")
        String message
) {

    public static ErrorResponse of(HttpStatus status, String message) {
        LocalDateTime timestamp = LocalDateTime.now();
        return new ErrorResponse(timestamp, status.value(), message);
    }
}
