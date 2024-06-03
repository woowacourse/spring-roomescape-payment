package roomescape.core.exception;

import java.io.IOException;
import org.springframework.http.HttpStatusCode;

public class PaymentException extends IOException {
    private final HttpStatusCode statusCode;

    public PaymentException(final HttpStatusCode statusCode, final String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
