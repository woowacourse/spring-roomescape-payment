package roomescape.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends RoomEscapeException {
    public NotFoundException(String errorCode, String message) {
        super(HttpStatus.NOT_FOUND, errorCode, message);
    }
}
