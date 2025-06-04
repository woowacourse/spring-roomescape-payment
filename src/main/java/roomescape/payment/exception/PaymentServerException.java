package roomescape.payment.exception;

public class PaymentServerException extends PaymentException {

    public PaymentServerException(final String code, final String message) {
        super(code, message);
    }
}
