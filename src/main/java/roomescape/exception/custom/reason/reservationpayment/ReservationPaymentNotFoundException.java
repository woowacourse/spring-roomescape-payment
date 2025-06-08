package roomescape.exception.custom.reason.reservationpayment;

import roomescape.exception.custom.status.NotFoundException;

public class ReservationPaymentNotFoundException extends NotFoundException {

    public ReservationPaymentNotFoundException() {
        super("결제 정보가 존재하지 않습니다.");
    }
}
