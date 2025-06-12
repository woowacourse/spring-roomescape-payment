package roomescape.reservation.exception;

import roomescape.common.exception.BusinessException;

public class InvalidApprovalException extends BusinessException {
    public InvalidApprovalException() {
        super(ReservationErrorCode.INVALID_RESERVATION_APPROVAL);
    }
}
