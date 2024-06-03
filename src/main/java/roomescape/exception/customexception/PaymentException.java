package roomescape.exception.customexception;

import org.springframework.http.HttpStatus;

public class PaymentException extends RuntimeException{

    private HttpStatus httpStatus;

    public PaymentException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
