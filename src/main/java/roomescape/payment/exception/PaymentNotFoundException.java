package roomescape.payment.exception;

import roomescape.common.exception.BusinessException;

public class PaymentNotFoundException extends BusinessException {
    public PaymentNotFoundException() {
        super(PaymentErrorCode.PAYMENT_NOT_FOUND);
    }
}
