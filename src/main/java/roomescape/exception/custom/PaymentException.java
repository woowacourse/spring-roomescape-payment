package roomescape.exception.custom;

public class PaymentException extends RuntimeException {

    public PaymentException(final String message) {
        super(message);
    }
}
