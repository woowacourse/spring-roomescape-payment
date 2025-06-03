package roomescape.exception.custom.reason.payment;

public class PaymentException extends RuntimeException {

    public PaymentException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public PaymentException(final String message) {
        super(message);
    }
}
