package roomescape.exception;

import org.springframework.http.HttpStatusCode;

public class PaymentException extends RuntimeException {

    private final HttpStatusCode statusCode;

    public PaymentException(String message, HttpStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
