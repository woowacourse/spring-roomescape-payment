package roomescape.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends RoomEscapeException {

    public ConflictException(ErrorCode errorCode) {
        super(HttpStatus.CONFLICT, errorCode.name(), errorCode.getMessage());
    }
}
