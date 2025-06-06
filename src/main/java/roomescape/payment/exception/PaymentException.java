package roomescape.payment.exception;

public class PaymentException extends RuntimeException {

    private final String code;

    public PaymentException(final String code, final String message) {
        super(message);
        this.code = code;
    }
}
