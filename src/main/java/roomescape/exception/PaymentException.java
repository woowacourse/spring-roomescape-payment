package roomescape.exception;

import org.springframework.http.HttpStatusCode;

public class PaymentException extends RuntimeException {

    private final HttpStatusCode status;
    private final String message;

    public PaymentException(HttpStatusCode status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    public HttpStatusCode getHttpStatus() {
        return this.status;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
