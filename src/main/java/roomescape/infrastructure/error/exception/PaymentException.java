package roomescape.infrastructure.error.exception;

public class PaymentException extends RuntimeException {

    public PaymentException(String message) {
        super(message);
    }
}
