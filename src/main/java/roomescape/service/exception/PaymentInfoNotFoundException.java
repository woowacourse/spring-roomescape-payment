package roomescape.service.exception;

import roomescape.exception.RoomescapeException;

public class PaymentInfoNotFoundException extends RoomescapeException {

    public PaymentInfoNotFoundException(final String message) {
        super(message);
    }
}
