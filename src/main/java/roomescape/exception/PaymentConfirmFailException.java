package roomescape.exception;

import org.springframework.http.HttpStatus;

public class PaymentConfirmFailException extends RuntimeException {

    private final HttpStatus status;

    public PaymentConfirmFailException(final String message, final HttpStatus statusCode) {
        super(message);
        this.status = statusCode;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
