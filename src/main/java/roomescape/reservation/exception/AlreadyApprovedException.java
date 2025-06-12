package roomescape.reservation.exception;

import roomescape.common.exception.BusinessException;

public class AlreadyApprovedException extends BusinessException {
    public AlreadyApprovedException() {
        super(ReservationErrorCode.RESERVATION_ALREADY_APPROVED);
    }
}
