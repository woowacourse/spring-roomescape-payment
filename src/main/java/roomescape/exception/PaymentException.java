package roomescape.exception;

import roomescape.exception.response.PaymentExceptionResponse;

public class PaymentException extends RuntimeException {
    private final PaymentExceptionResponse paymentExceptionResponse;

    public PaymentException(PaymentExceptionResponse paymentExceptionResponse) {
        super(paymentExceptionResponse.getMessage());
        this.paymentExceptionResponse = paymentExceptionResponse;
    }

    public PaymentExceptionResponse getPaymentExceptionResponse() {
        return paymentExceptionResponse;
    }
}
