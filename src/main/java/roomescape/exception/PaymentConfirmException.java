package roomescape.exception;

import roomescape.exception.model.InternalExceptionCode;
import roomescape.exception.model.PaymentConfirmExceptionCode;

public class PaymentConfirmException extends RoomEscapeException{

    public PaymentConfirmException(PaymentConfirmExceptionCode paymentConfirmExceptionCode) {
        super(paymentConfirmExceptionCode);
    }

    public PaymentConfirmException(InternalExceptionCode internalExceptionCode) {
        super(internalExceptionCode);
    }
}
