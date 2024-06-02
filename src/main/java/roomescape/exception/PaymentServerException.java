package roomescape.exception;

public class PaymentServerException extends RuntimeException {

    public PaymentServerException(String message) {
        super(message);
    }

    public PaymentServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
