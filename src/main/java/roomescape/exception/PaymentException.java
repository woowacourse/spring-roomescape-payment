package roomescape.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class PaymentException extends CustomException {
    public PaymentException(String message, HttpStatusCode status) {
        super(message, status);
    }
}
