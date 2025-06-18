package roomescape.payment.exception;

import roomescape.common.exception.ValidationException;

public class InvalidPaymentException extends ValidationException {

    public InvalidPaymentException(String message) {
        super(message);
    }
} 
