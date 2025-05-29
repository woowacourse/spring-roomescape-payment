package roomescape.infrastructure.error.exception;

public class PaymentException extends RuntimeException {

    public PaymentException(final String message) {
        super(message);
    }

}
