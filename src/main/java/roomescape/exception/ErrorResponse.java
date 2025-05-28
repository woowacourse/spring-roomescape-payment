package roomescape.exception;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
        String errorCode,
        HttpStatus status,
        String message
) {

    public static ErrorResponse from(RoomEscapeException exception) {
        return new ErrorResponse(exception.getErrorCode(), exception.getHttpStatus(), exception.getMessage());
    }
}
