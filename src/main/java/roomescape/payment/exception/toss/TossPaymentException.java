package roomescape.payment.exception.toss;

import roomescape.exception.ErrorType;
import roomescape.exception.ExceptionResponse;
import roomescape.exception.InternalException;
import roomescape.exception.PaymentException;

public class TossPaymentException extends PaymentException {

    public TossPaymentException(TossPaymentErrorResponse tossPaymentErrorResponse) {
        super(filterError(tossPaymentErrorResponse));
    }

    private static ExceptionResponse filterError(TossPaymentErrorResponse response) {
        TossPaymentErrorType errorType = TossPaymentErrorType.valueOf(response.code());
        if (errorType.isExpose()) {
            return response.toExceptionResponse();
        }
        throw new InternalException(ErrorType.PAYMENT_ERROR);
    }
}

