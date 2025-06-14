package roomescape.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends RoomEscapeException {
    public ForbiddenException(String errorCode, String message) {
        super(HttpStatus.FORBIDDEN, errorCode, message);
    }

    public ForbiddenException(ErrorCode errorCode) {
        super(HttpStatus.FORBIDDEN, errorCode.name(), errorCode.getMessage());
    }
}
