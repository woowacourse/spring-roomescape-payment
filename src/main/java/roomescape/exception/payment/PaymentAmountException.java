package roomescape.exception.payment;

import org.springframework.http.HttpStatus;
import roomescape.exception.CustomException;

public class PaymentAmountException extends CustomException {

    private static final String ERROR_MESSAGE = "결제 금액은 0 이하일 수 없습니다.";

    public PaymentAmountException() {
        super(ERROR_MESSAGE, HttpStatus.BAD_REQUEST);
    }

    public PaymentAmountException(Throwable cause) {
        super(ERROR_MESSAGE, cause, HttpStatus.BAD_REQUEST);
    }
}
