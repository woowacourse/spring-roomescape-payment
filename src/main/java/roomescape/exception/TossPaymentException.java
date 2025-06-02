package roomescape.exception;

public class TossPaymentException extends RuntimeException {
    public TossPaymentException(String message, String code) {
        super(message);
    }
}
