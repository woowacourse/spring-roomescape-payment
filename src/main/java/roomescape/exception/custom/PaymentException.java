package roomescape.exception.custom;

import org.springframework.http.HttpStatus;

public class PaymentException extends RuntimeException{

    private final HttpStatus status;

    public PaymentException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
