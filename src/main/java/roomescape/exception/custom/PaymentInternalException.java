package roomescape.exception.custom;

public class PaymentInternalException extends RuntimeException {

    private final Exception exception;

    public PaymentInternalException(final Exception exception, final String message) {
        super(message);
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }
}
