package roomescape.common.exception.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp
) {
    public ErrorResponse(HttpStatus status, String message) {
        this(status.value(), message, LocalDateTime.now());
    }
}
