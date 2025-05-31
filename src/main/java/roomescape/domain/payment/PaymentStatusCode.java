package roomescape.domain.payment;

public enum PaymentStatusCode {
    FAILED_PAYMENT(Cause.CLIENT_ERROR),
    INVALID_AUTH_CREDENTIALS(Cause.SERVER_ERROR),
    FAILED_INTERNAL_PROCESSING(Cause.EXTERNAL_ERROR);

    private final Cause cause;

    PaymentStatusCode(final Cause cause) {
        this.cause = cause;
    }

    public boolean causedBy(final Cause cause) {
        return this.cause == cause;
    }

    public enum Cause {
        CLIENT_ERROR,
        SERVER_ERROR,
        EXTERNAL_ERROR
    }
}
