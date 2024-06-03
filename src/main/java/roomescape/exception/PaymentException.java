package roomescape.exception;

public class PaymentException extends ApplicationException {

    public PaymentException(String errorCode) {
        super(errorCode);
    }

    public static PaymentException tossPaymentExceptionOf(String errorCode) {
        TossPaymentErrorCode tossPaymentErrorCode = TossPaymentErrorCode.of(errorCode);
        return new PaymentException(tossPaymentErrorCode.getErrorMessage());
    }
}
