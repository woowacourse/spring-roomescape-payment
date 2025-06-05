package roomescape.common.exception.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        String code,
        String message,
        LocalDateTime timestamp
) {

    public ErrorResponse(final String code, final String message) {
        this(code, message, LocalDateTime.now());
    }
}