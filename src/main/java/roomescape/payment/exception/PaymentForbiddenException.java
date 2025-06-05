package roomescape.payment.exception;

public class PaymentForbiddenException extends PaymentClientException {

    public PaymentForbiddenException(final String code, final String message) {
        super(code, message);
    }
}
