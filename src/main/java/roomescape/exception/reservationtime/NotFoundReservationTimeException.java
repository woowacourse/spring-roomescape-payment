package roomescape.exception.reservationtime;

import org.springframework.http.HttpStatus;
import roomescape.exception.common.RoomescapeException;

public class NotFoundReservationTimeException extends RoomescapeException {
    public NotFoundReservationTimeException() {
        super("존재하지 않는 시간입니다.", HttpStatus.NOT_FOUND);
    }
}
