package roomescape.exception.payment;

import org.springframework.http.HttpStatus;
import roomescape.exception.ApplicationException;

public class PaymentExistsException extends ApplicationException {

    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;
    private static final String MESSAGE = "결제가 존재합니다.";

    public PaymentExistsException() {
        super(MESSAGE, STATUS);
    }
}
