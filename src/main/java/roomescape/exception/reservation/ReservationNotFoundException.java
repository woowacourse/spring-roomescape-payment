package roomescape.exception.reservation;

import org.springframework.http.HttpStatus;
import roomescape.exception.ApplicationException;

public class ReservationNotFoundException extends ApplicationException {

    private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;
    private static final String MESSAGE = "존재하지 않는 예약입니다.";

    public ReservationNotFoundException() {
        super(MESSAGE, STATUS);
    }
}
