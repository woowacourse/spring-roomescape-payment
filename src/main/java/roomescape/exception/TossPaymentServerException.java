package roomescape.exception;

import roomescape.service.payment.dto.PaymentErrorResult;

public class TossPaymentServerException extends PaymentException {

    public TossPaymentServerException(String message) {
        super(message);
    }

    public TossPaymentServerException(PaymentErrorResult paymentErrorResult) {
        super(paymentErrorResult);
    }

}
