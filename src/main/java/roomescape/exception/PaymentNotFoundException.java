package roomescape.exception;

import org.springframework.http.HttpStatus;

public class PaymentNotFoundException extends CustomException {

    private static final String MESSAGE = "결제 내역이 존재하지 않습니다.";
    private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

    public PaymentNotFoundException() {
        super(MESSAGE, STATUS);
    }
}
