package roomescape.approval.exception.payment;

import roomescape.common.exception.BusinessException;

public class PaymentNotFoundException extends BusinessException {
    public PaymentNotFoundException() {
        super(PaymentErrorCode.PAYMENT_NOT_FOUND);
    }
}
