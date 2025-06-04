package roomescape.payment.exception;

public class PaymentClientException extends PaymentException {

    public PaymentClientException(final String code, final String message) {
        super(code, message);
    }
}
