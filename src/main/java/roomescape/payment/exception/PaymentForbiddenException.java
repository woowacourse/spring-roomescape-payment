package roomescape.payment.exception;

public class PaymentForbiddenException extends PaymentException {

    public PaymentForbiddenException(final String code, final String message) {
        super(code, message);
    }
}
