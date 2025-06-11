package roomescape.exception.reservation;

import org.springframework.http.HttpStatus;
import roomescape.exception.ApplicationException;

public class PastDateReservationException extends ApplicationException {

    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;
    private static final String MESSAGE = "과거 날짜로 예약할 수 없습니다.";

    public PastDateReservationException() {
        super(MESSAGE, STATUS);
    }
}
