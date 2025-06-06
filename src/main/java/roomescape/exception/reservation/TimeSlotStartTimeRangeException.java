package roomescape.exception.reservation;

import java.time.LocalTime;
import org.springframework.http.HttpStatus;
import roomescape.exception.ApplicationException;

public class TimeSlotStartTimeRangeException extends ApplicationException {

    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;
    private static final String MESSAGE_FORMAT = "시작 시간은 %s~%s 만 가능합니다.";

    public TimeSlotStartTimeRangeException(LocalTime rangeStart, LocalTime rangeEnd) {
        super(String.format(MESSAGE_FORMAT, rangeStart, rangeEnd), STATUS);
    }
}
