package roomescape.exception.reservation;

import org.springframework.http.HttpStatus;
import roomescape.exception.ApplicationException;

public class ReservationExistsException extends ApplicationException {

    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;
    private static final String MESSAGE = "예약이 존재합니다.";

    public ReservationExistsException() {
        super(MESSAGE, STATUS);
    }
}
