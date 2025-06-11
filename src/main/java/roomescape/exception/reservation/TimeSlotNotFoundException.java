package roomescape.exception.reservation;

import org.springframework.http.HttpStatus;
import roomescape.exception.ApplicationException;

public class TimeSlotNotFoundException extends ApplicationException {

    private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;
    private static final String MESSAGE = "존재하지 않는 예약 시간입니다.";

    public TimeSlotNotFoundException() {
        super(MESSAGE, STATUS);
    }
}
