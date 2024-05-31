package roomescape.exception.reservation;

import org.springframework.http.HttpStatus;
import roomescape.exception.CustomException;

public class CancelReservationException extends CustomException {
    public CancelReservationException() {
        super("이미 취소된 예약입니다.", HttpStatus.BAD_REQUEST);
    }

    public CancelReservationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
