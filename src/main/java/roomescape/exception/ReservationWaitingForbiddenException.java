package roomescape.exception;

import roomescape.exception.common.ForbiddenException;

public class ReservationWaitingForbiddenException extends ForbiddenException {
    public ReservationWaitingForbiddenException(String message) {
        super(message, "[예약 대기 거절] 실패 사유 : " + message);
    }

    public ReservationWaitingForbiddenException(String message, Long memberId, Long reservationId) {
        super(message,
                String.format("[예약 대기 거절] 실패 사유 : %s, 유저 id : %d, 존재하는 예약 id : %d",
                        message, memberId, reservationId));
    }
}
