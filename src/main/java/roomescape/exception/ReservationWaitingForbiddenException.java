package roomescape.exception;

import roomescape.exception.common.ForbiddenException;

public class ReservationWaitingForbiddenException extends ForbiddenException {
    public ReservationWaitingForbiddenException(String message) {
        super(message, "[예약 대기 거절] 실패 사유 : " + message);
    }
}
