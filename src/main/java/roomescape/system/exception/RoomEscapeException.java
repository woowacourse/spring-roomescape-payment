package roomescape.system.exception;

import java.util.Optional;
import org.springframework.http.HttpStatusCode;

public class RoomEscapeException extends RuntimeException {
    private final ErrorType errorType;
    private final String message;
    private final String invalidValue;
    private final HttpStatusCode httpStatus;

    public RoomEscapeException(ErrorType errorType, HttpStatusCode httpStatus) {
        this(errorType, null, httpStatus);
    }

    public RoomEscapeException(ErrorType errorType, String invalidValue, HttpStatusCode httpStatus) {
        this.errorType = errorType;
        this.message = errorType.getDescription();
        this.invalidValue = invalidValue;
        this.httpStatus = httpStatus;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public HttpStatusCode getHttpStatus() {
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
