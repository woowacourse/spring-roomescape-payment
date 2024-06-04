package roomescape.exception;

public class PaymentCredentialMissMatchException extends RuntimeException {

    public PaymentCredentialMissMatchException(final String message) {
        super(message);
    }
}
