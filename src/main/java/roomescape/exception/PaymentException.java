package roomescape.exception;

import org.springframework.http.HttpStatus;

public class PaymentException extends CustomException {

    public PaymentException(final String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
