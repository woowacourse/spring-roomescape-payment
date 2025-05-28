package roomescape.payment.exception;

import roomescape.global.exception.InvalidInputException;

public class InvalidPaymentException extends InvalidInputException {

    private static final String DEFAULT_MESSAGE = "유효하지 않는 결제 요청입니다.";

    public InvalidPaymentException(String message) {
        super(message);
    }

    public InvalidPaymentException() {
        this(DEFAULT_MESSAGE);
    }
}
