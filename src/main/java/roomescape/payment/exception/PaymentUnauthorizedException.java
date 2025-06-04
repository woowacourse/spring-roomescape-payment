package roomescape.payment.exception;

public class PaymentUnauthorizedException extends PaymentException {

    public PaymentUnauthorizedException(final String code, final String message) {
        super(code, message);
    }
}
