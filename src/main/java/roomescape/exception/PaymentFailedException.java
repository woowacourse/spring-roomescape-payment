package roomescape.exception;

public class PaymentFailedException extends RuntimeException {

    private final Cause cause;

    public PaymentFailedException(final Cause cause, final String message) {
        super(message);
        this.cause = cause;
    }

    public boolean causedByClient() {
        return cause == Cause.CLIENT_ERROR;
    }

    public boolean causedByServer() {
        return cause == Cause.SERVER_ERROR;
    }

    public boolean causedByExternalServer() {
        return cause == Cause.EXTERNAL_ERROR;
    }

    public enum Cause {
        CLIENT_ERROR,
        SERVER_ERROR,
        EXTERNAL_ERROR
    }
}
