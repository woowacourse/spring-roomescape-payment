package roomescape.payment.exception;

import roomescape.global.common.exception.NotFoundException;

public class PaymentNotFoundException extends NotFoundException {

    public PaymentNotFoundException(final String message) {
        super(message);
    }
}
