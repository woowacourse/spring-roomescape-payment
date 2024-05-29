package roomescape.exception;

public class PaymentException extends RuntimeException {

    private final String code;

    public PaymentException(String code, String message) {
        super(message);
        this.code = code;
    }
}
