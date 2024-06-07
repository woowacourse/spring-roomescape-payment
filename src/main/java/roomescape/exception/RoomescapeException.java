package roomescape.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class RoomescapeException extends RuntimeException {
    private final ErrorType errorType;

    public RoomescapeException(final ErrorType errorType) {
        this(errorType, null);
    }

    public RoomescapeException(final ErrorType errorType, final Throwable cause) {
        super(errorType.name(), cause);
        this.errorType = errorType;
    }

    public HttpStatusCode getStatusCode() {
        return HttpStatus.valueOf(errorType.getStatusCode());
    }

    public String getErrorCode() {
        return errorType.getErrorCode();
    }

    @Override
    public String getMessage() {
        return errorType.getMessage();
    }
}
