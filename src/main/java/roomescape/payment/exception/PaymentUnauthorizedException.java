package roomescape.payment.exception;

public class PaymentUnauthorizedException extends PaymentException {

    public PaymentUnauthorizedException(final String message) {
        super(message);
    }
}
