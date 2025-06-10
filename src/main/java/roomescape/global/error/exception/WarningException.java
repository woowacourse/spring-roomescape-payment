package roomescape.global.error.exception;

import org.springframework.http.HttpStatus;

public class WarningException extends RuntimeException {

    private final HttpStatus httpStatus;

    public WarningException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public WarningException(String message) {
        this(message, HttpStatus.BAD_REQUEST);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
