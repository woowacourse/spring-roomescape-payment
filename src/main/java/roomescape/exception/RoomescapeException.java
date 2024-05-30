package roomescape.exception;

import org.springframework.http.HttpStatusCode;

public class RoomescapeException extends RuntimeException {

    private final RoomescapeExceptionCode exceptionCode;

    public RoomescapeException(final RoomescapeExceptionCode exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    public HttpStatusCode getHttpStatusCode() {
        return exceptionCode.getHttpStatusCode();
    }

    @Override
    public String getMessage() {
        return exceptionCode.getMessage();
    }
}
