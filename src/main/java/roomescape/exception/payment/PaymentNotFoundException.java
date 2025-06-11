package roomescape.exception.payment;

import org.springframework.http.HttpStatus;
import roomescape.exception.ApplicationException;

public class PaymentNotFoundException extends ApplicationException {

    private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;
    private static final String MESSAGE = "존재하지 않는 결제입니다.";

    public PaymentNotFoundException() {
        super(MESSAGE, STATUS);
    }
}
