package roomescape.exception;

public class PaymentFailedException extends RuntimeException {

    public PaymentFailedException(final String message) {
        super(message);
    }
}
