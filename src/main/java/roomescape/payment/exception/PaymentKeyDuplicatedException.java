package roomescape.payment.exception;

import roomescape.common.exception.DuplicatedException;

public class PaymentKeyDuplicatedException extends DuplicatedException {

    public PaymentKeyDuplicatedException(String message) {
        super(message);
    }
}
