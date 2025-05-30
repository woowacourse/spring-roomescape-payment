package roomescape.payment.exception.custom;

public class PaymentConnectionException extends RuntimeException {
    public PaymentConnectionException(final String message) {
        super(message);
    }
}
