package roomescape.exception.payment;

import org.springframework.http.HttpStatus;
import roomescape.exception.ApplicationException;

public class PaymentIntegrityViolationException extends ApplicationException {

    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;
    private static final String MESSAGE = "결제 정보가 일치하지 않습니다.";

    public PaymentIntegrityViolationException() {
        super(MESSAGE, STATUS);
    }
}
