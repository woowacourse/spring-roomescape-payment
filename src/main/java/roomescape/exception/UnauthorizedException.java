package roomescape.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends RoomEscapeException {
    public UnauthorizedException(String errorCode, String message) {
        super(HttpStatus.UNAUTHORIZED, errorCode, message);
    }

    public UnauthorizedException(ErrorCode errorCode) {
        super(HttpStatus.UNAUTHORIZED, errorCode.name(), errorCode.getMessage());
    }
}
