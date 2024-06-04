package roomescape.exception;

public class PaymentFailureException extends RuntimeException {

    public PaymentFailureException(String message) {
        super(message);
    }
}
