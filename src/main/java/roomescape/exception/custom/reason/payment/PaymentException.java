package roomescape.exception.custom.reason.payment;

import org.springframework.http.HttpStatus;
import roomescape.exception.custom.status.CustomException;

public class PaymentException extends CustomException {

    public PaymentException(String message, Throwable cause) {
        super(message, cause, HttpStatus.BAD_REQUEST);
    }

    public PaymentException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
