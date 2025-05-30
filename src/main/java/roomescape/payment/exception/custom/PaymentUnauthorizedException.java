package roomescape.payment.exception.custom;

public class PaymentUnauthorizedException extends RuntimeException {
    public PaymentUnauthorizedException(final String message) {
        super(message);
    }
}
