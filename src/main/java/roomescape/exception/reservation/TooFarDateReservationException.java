package roomescape.exception.reservation;

import org.springframework.http.HttpStatus;
import roomescape.exception.ApplicationException;

public class TooFarDateReservationException extends ApplicationException {

    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;
    private static final String MESSAGE_FORMAT = "%d일 전부터 예약할 수 있습니다.";

    public TooFarDateReservationException(int date) {
        super(String.format(MESSAGE_FORMAT, date), STATUS);
    }
}
