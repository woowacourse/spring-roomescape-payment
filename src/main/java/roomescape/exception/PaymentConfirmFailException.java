package roomescape.exception;

public class PaymentConfirmFailException extends RuntimeException {
    public PaymentConfirmFailException(final String message) {
        super(message);
    }
}
