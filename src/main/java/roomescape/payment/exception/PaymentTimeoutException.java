package roomescape.payment.exception;

public class PaymentTimeoutException extends RuntimeException {
    public PaymentTimeoutException(final String message) {
        super(message);
    }
}
