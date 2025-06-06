package roomescape.exception.reservation;

import org.springframework.http.HttpStatus;
import roomescape.exception.ApplicationException;

public class WaitingNotFoundException extends ApplicationException {

    private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;
    private static final String MESSAGE = "존재하지 않는 예약 대기입니다.";

    public WaitingNotFoundException() {
        super(MESSAGE, STATUS);
    }
}
