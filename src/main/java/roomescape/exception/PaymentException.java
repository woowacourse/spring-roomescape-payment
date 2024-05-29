package roomescape.exception;

import org.springframework.http.HttpStatusCode;

public class PaymentException extends RuntimeException {

    private final HttpStatusCode statusCode;

    public PaymentException(HttpStatusCode statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
