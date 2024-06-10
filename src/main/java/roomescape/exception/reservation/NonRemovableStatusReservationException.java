package roomescape.exception.reservation;

import org.springframework.http.HttpStatus;
import roomescape.exception.common.RoomescapeException;

public class NonRemovableStatusReservationException extends RoomescapeException {
    public NonRemovableStatusReservationException() {
        super("취소 또는 결제 대기 상태의 예약만 삭제할 수 있습니다.", HttpStatus.BAD_REQUEST);
    }
}
