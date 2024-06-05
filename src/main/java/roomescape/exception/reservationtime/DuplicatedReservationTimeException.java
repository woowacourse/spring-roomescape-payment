package roomescape.exception.reservationtime;

import org.springframework.http.HttpStatus;
import roomescape.exception.common.RoomescapeException;

public class DuplicatedReservationTimeException extends RoomescapeException {
    public DuplicatedReservationTimeException() {
        super("해당 예약 시간이 이미 존재합니다.", HttpStatus.CONFLICT);
    }
}
