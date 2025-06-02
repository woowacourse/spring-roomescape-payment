package roomescape.payment.exception.custom;

public class PaymentTimeoutException extends RuntimeException {
    public PaymentTimeoutException(final String message) {
        super(message);
    }
}
