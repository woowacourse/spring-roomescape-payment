package roomescape.payment.exception;

public class PaymentException extends RuntimeException {

    private final String code;
    private final String message;

    public PaymentException(final String code, final String message) {
        this.code = code;
        this.message = message;
    }
}
