package roomescape.global.exception.custom;

import org.springframework.http.HttpStatusCode;

public class TossPaymentsException extends RuntimeException {
    private final HttpStatusCode statusCode;

    public TossPaymentsException(final HttpStatusCode statusCode, final String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
