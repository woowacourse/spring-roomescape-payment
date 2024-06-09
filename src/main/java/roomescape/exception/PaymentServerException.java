package roomescape.exception;

public class PaymentServerException extends PaymentException {

    public PaymentServerException(String message) {
        super(message);
    }

    public PaymentServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
