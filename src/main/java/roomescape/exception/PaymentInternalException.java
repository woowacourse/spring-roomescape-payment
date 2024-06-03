package roomescape.exception;

public class PaymentInternalException extends RuntimeException {

    private final String exceptionClass;

    public PaymentInternalException(final String exceptionClass, final String message) {
        super(message);
        this.exceptionClass = exceptionClass;
    }

    public String getExceptionClass() {
        return exceptionClass;
    }
}
