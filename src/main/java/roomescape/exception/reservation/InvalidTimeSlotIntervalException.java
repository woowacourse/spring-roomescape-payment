package roomescape.exception.reservation;

import org.springframework.http.HttpStatus;
import roomescape.exception.ApplicationException;

public class InvalidTimeSlotIntervalException extends ApplicationException {

    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;
    private static final String MESSAGE = "예약 시간은 30분 간격으로만 생성할 수 있습니다.";

    public InvalidTimeSlotIntervalException() {
        super(MESSAGE, STATUS);
    }
}
