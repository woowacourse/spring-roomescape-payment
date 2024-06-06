package roomescape.exception;

import roomescape.service.payment.dto.PaymentErrorResult;

public class PaymentException extends RuntimeException {

    public PaymentException(String message) {
        super(message);
    }

    public PaymentException(PaymentErrorResult paymentErrorResult) {
        super(paymentErrorResult.message());
    }
}
