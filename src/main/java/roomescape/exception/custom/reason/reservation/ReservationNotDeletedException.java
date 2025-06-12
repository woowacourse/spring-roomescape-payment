package roomescape.exception.custom.reason.reservation;

import roomescape.exception.custom.status.BadRequestException;

public class ReservationNotDeletedException extends BadRequestException {
    public ReservationNotDeletedException(String message) {
        super(message);
    }

    public static ReservationNotDeletedException paymentNotRefunded() {
        return new ReservationNotDeletedException("결제를 취소하기 이전에는 예약을 삭제할 수 없습니다. 결제를 먼저 취소하고 재시도 해주세요.");
    }
}
