package roomescape.global.dto;

import org.springframework.http.HttpStatus;

public record ExternalApiErrorResponse(
        HttpStatus status,
        String message
) {
}
