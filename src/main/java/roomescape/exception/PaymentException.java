package roomescape.exception;

import roomescape.payment.exception.toss.TossPaymentErrorResponse;

public class PaymentException extends RuntimeException {

    protected final String code;

    protected final String message;

    public PaymentException(TossPaymentErrorResponse tossPaymentErrorResponse) {
        super(tossPaymentErrorResponse.message());
        this.code = tossPaymentErrorResponse.code();
        this.message = tossPaymentErrorResponse.message();
    }

    public ExceptionResponse toExceptionResponse() {
        return new ExceptionResponse(code, message);
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
