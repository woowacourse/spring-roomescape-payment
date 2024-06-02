package roomescape.exception.payment;

import org.springframework.http.HttpStatus;
import roomescape.exception.CustomException;

public class PaymentServerException extends CustomException {
    public PaymentServerException() {
        super("결제에 실패했어요. 같은 문제가 반복된다면 관리자에게 문의해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public PaymentServerException(String message, Throwable cause) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}
