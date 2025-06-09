package roomescape.payment.exception;

import roomescape.common.exception.BusinessException;

public class PaymentException extends BusinessException {

    public PaymentException(final String message) {
        super(message);
    }
}
