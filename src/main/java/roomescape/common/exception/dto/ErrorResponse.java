package roomescape.common.exception.dto;

import java.time.ZonedDateTime;

public record ErrorResponse(
        String code,
        String message,
        ZonedDateTime timestamp
) {

    public ErrorResponse(final String code, final String message) {
        this(code, message, ZonedDateTime.now());
    }
}