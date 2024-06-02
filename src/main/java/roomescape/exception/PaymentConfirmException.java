package roomescape.exception;

import roomescape.exception.global.GlobalExceptionCode;
import roomescape.exception.model.PaymentConfirmExceptionCode;

public class PaymentConfirmException extends RoomEscapeException{

    public PaymentConfirmException(PaymentConfirmExceptionCode paymentConfirmExceptionCode) {
        super(paymentConfirmExceptionCode);
    }

    public PaymentConfirmException(GlobalExceptionCode globalExceptionCode) {
        super(globalExceptionCode);
    }
}
