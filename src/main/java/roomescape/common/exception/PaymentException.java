package roomescape.common.exception;

import org.springframework.http.HttpStatusCode;

public class PaymentException extends RuntimeException {
    private final int statusCode;

    public PaymentException(HttpStatusCode httpStatusCode, String message) {
        super(message);
        this.statusCode = httpStatusCode.value();
    }

    public int getStatusCode() {
        return statusCode;
    }
}
