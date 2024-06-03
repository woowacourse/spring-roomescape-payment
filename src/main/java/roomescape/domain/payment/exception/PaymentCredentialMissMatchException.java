package roomescape.domain.payment.exception;

public class PaymentCredentialMissMatchException extends RuntimeException {
    public PaymentCredentialMissMatchException(final String message) {
        super(message);
    }

    public PaymentCredentialMissMatchException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
