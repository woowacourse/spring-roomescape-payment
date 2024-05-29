package roomescape.exception;

public class PaymentException extends RuntimeException{
    public PaymentException(final String message) {
        super(message);
    }
}
