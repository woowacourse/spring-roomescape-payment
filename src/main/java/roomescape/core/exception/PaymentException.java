package roomescape.core.exception;

import org.springframework.http.HttpStatusCode;

public class PaymentException extends RuntimeException {
    private final HttpStatusCode statusCode;

    public PaymentException(final HttpStatusCode statusCode, final String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public PaymentException(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
