package roomescape.exception;

import static roomescape.exception.RoomescapeExceptionCode.TOSS_PAYMENTS_ERROR;

import org.springframework.http.HttpStatusCode;

public class TossPaymentsException extends RoomescapeException {

    private final HttpStatusCode statusCode;
    private final String message;

    public TossPaymentsException(HttpStatusCode statusCode, String message) {
        super(TOSS_PAYMENTS_ERROR);
        this.statusCode = statusCode;
        this.message = message;
    }

    @Override
    public HttpStatusCode getHttpStatusCode() {
        return statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
