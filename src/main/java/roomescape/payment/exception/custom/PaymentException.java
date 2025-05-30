package roomescape.payment.exception.custom;

public class PaymentException extends RuntimeException {
    public PaymentException(final String message) {
        super(message);
    }
}
