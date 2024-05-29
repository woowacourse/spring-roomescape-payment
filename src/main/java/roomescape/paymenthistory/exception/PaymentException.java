package roomescape.paymenthistory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class PaymentException extends RuntimeException {

    private final HttpStatusCode httpStatus;

    public PaymentException(String message, HttpStatusCode httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public static class PaymentServerError extends PaymentException {
        public PaymentServerError(String message) {
            super(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatus;
    }
}
