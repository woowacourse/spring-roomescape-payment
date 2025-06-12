package roomescape.approval.exception.payment;

import roomescape.common.exception.BusinessException;

public class InvalidPaymentAmountException extends BusinessException {
    public InvalidPaymentAmountException() {
        super(PaymentErrorCode.INCORRECT_PAYMENT_AMOUNT);
    }
}
