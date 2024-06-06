package roomescape.exception.payment;

import org.springframework.http.HttpStatus;
import roomescape.exception.CustomException;

public class PaymentFailException extends CustomException {
    public PaymentFailException(String message, HttpStatus status) {
        super(message, status);
    }

    public PaymentFailException(String message, Throwable cause, HttpStatus status) {
        super(message, cause, status);
    }
}
