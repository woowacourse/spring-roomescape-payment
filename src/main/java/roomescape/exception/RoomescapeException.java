package roomescape.exception;

import org.springframework.http.HttpStatusCode;

public class RoomescapeException extends RuntimeException {

    private final RoomescapeErrorCode exceptionCode;

    public RoomescapeException(final RoomescapeErrorCode exceptionCode) {
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
