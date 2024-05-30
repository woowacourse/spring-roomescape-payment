package roomescape.exception;

import static roomescape.exception.RoomescapeExceptionCode.TOSS_PAYMENT_ERROR;

import org.springframework.http.HttpStatusCode;

public class TossPaymentException extends RoomescapeException {

    private final HttpStatusCode statusCode;
    private final String message;

    public TossPaymentException(HttpStatusCode statusCode, String message) {
        super(TOSS_PAYMENT_ERROR);
        this.statusCode = statusCode;
        this.message = message;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
