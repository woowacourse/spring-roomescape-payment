package roomescape.exception.reservation;

import org.springframework.http.HttpStatus;
import roomescape.exception.common.RoomescapeException;

public class ReservationAuthorityNotExistException extends RoomescapeException {
    public ReservationAuthorityNotExistException() {
        super("해당 예약의 예약자가 아닙니다.", HttpStatus.FORBIDDEN);
    }
}
