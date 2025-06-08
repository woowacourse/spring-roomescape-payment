package roomescape.payment.exception;

import roomescape.common.exception.BusinessException;

public class PaymentKeyRequiredException extends BusinessException {
    public PaymentKeyRequiredException() {
        super(PaymentErrorCode.PAYMENT_KEY_LENGTH_EXCEEDED);
    }
}
