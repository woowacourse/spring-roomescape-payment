package roomescape.payment.exception;

import roomescape.common.exception.BusinessException;

public class PaymentSessionExpiredException extends BusinessException {
    public PaymentSessionExpiredException() {
        super(PaymentErrorCode.PAYMENT_SESSION_EXPRIED);
    }
}
