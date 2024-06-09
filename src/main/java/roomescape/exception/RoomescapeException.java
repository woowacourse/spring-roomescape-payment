package roomescape.exception;

import org.springframework.http.HttpStatusCode;

public class RoomescapeException extends RuntimeException {

    private final ErrorCodeWithHttpStatusCode exceptionCode;

    public RoomescapeException(final ErrorCodeWithHttpStatusCode exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    public HttpStatusCode getHttpStatusCode() {
        return exceptionCode.httpStatusCode();
    }

    @Override
    public String getMessage() {
        return exceptionCode.message();
    }
}
