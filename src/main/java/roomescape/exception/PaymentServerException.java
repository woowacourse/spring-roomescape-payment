package roomescape.exception;

public class PaymentServerException extends RoomEscapeException {
    public PaymentServerException(String message) {
        super(message);
    }

    public PaymentServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
