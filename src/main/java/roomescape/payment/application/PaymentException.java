package roomescape.payment.application;

import org.springframework.http.HttpStatus;

public class PaymentException extends RuntimeException {

    private final HttpStatus status;

    public PaymentException(final HttpStatus status, final String message) {
        super(message);
        this.status = status;
    }

    public boolean is5xxServerError() {
        return status.is5xxServerError();
    }
}
