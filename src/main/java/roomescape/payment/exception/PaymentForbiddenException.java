package roomescape.payment.exception;

public class PaymentForbiddenException extends PaymentClientException {

    public PaymentForbiddenException(final String message) {
        super(message);
    }
}
