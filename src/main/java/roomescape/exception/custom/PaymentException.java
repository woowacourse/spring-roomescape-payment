package roomescape.exception.custom;

import org.springframework.http.HttpStatusCode;

public class PaymentException extends IllegalArgumentException {

    private final HttpStatusCode statusCode;

    public PaymentException(final HttpStatusCode statusCode, final String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
