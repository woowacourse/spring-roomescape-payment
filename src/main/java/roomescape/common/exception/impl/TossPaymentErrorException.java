package roomescape.common.exception.impl;

import org.springframework.http.HttpStatus;
import roomescape.common.exception.RoomescapeException;

public class TossPaymentErrorException extends RoomescapeException {

    private final HttpStatus status;

    public TossPaymentErrorException(final HttpStatus status, final String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
