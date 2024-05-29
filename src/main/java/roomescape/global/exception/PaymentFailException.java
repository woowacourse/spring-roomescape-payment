package roomescape.global.exception;

public class PaymentFailException extends RuntimeException {

    public PaymentFailException(String message) {
        super(message);
    }

    public PaymentFailException(String message, Throwable cause) {
        super(message, cause);
    }
}
