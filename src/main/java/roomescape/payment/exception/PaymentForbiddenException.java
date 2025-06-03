package roomescape.payment.exception;

public class PaymentForbiddenException extends PaymentException {

    public PaymentForbiddenException(final String message) {
        super(message);
    }
}
