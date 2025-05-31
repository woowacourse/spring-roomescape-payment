package roomescape.domain.payment;

public enum PaymentFailCode {
    CONDITION_NOT_SATISFIED(Cause.CLIENT_ERROR),
    INVALID_AUTH_CREDENTIALS(Cause.SERVER_ERROR),
    EXTERNAL_SERVER_PROCESSING(Cause.EXTERNAL_ERROR);

    private final Cause cause;

    PaymentFailCode(final Cause cause) {
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
