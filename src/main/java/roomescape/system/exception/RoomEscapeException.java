package roomescape.system.exception;

import java.util.Optional;
import org.springframework.http.HttpStatus;

public class RoomEscapeException extends RuntimeException {
    private final ErrorType errorType;
    private final String message;
    private final String invalidValue;
    private final HttpStatus httpStatus;

    public RoomEscapeException(ErrorType errorType, HttpStatus httpStatus) {
        this(errorType, null, httpStatus);
    }

    public RoomEscapeException(ErrorType errorType, String invalidValue, HttpStatus httpStatus) {
        this.errorType = errorType;
        this.message = errorType.getDescription();
        this.invalidValue = invalidValue;
        this.httpStatus = httpStatus;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public Optional<String> getInvalidValue() {
        return Optional.ofNullable(invalidValue);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
