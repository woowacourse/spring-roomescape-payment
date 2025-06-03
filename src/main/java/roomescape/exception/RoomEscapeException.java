package roomescape.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RoomEscapeException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String errorCode;

    public RoomEscapeException(HttpStatus httpStatus, String errorCode, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }
}
