package roomescape.payment.exception;

import roomescape.common.exception.BusinessException;

public class InvalidPaymentAmountException extends BusinessException {
    public InvalidPaymentAmountException() {
        super(PaymentErrorCode.INCORRECT_PAYMENT_AMOUNT);
    }
}
