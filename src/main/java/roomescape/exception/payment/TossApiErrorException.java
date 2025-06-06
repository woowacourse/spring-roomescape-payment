package roomescape.exception.payment;

import org.springframework.http.HttpStatus;
import roomescape.exception.ApplicationException;

public class TossApiErrorException extends ApplicationException {

    private static final HttpStatus STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    public TossApiErrorException(String message) {
        super(message, STATUS);
    }
}
