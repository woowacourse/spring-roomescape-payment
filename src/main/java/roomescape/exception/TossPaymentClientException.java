package roomescape.exception;

import roomescape.service.payment.dto.PaymentErrorResult;

public class TossPaymentClientException extends PaymentException {

    public TossPaymentClientException(String message) {
        super(message);
    }

    public TossPaymentClientException(PaymentErrorResult paymentErrorResult) {
        super(paymentErrorResult);
    }

}
