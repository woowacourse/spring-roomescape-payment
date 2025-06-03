package roomescape.exception;

import org.springframework.http.HttpStatus;

public class InternalServerException extends RoomEscapeException {
    public InternalServerException(String errorCode, String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, errorCode, message);
    }
}
