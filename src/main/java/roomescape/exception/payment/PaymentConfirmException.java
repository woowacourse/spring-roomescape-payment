package roomescape.exception.payment;

import roomescape.exception.common.RoomescapeException;

public class PaymentConfirmException extends RoomescapeException {
    public PaymentConfirmException(PaymentConfirmErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode.getHttpStatus());
    }
}
