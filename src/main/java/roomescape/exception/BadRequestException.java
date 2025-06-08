package roomescape.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends RoomEscapeException {

    public BadRequestException(String errorCode, String message) {
        super(HttpStatus.BAD_REQUEST, errorCode, message);
    }

    public BadRequestException(ErrorCode errorCode) {
        super(HttpStatus.BAD_REQUEST, errorCode.name(), errorCode.getMessage());
    }
}
