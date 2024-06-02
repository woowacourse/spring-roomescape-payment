package roomescape.exception.payment;

import org.springframework.http.HttpStatus;
import roomescape.exception.CustomException;

public class PaymentClientException extends CustomException {
    public PaymentClientException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
