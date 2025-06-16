package roomescape.payment.exception;

import roomescape.common.exception.RoomescapeException;

public class PaymentException extends RoomescapeException {

    public PaymentException(final String message) {
        super(message);
    }
}
