package roomescape.payment.exception.custom;

public class PaymentClientErrorException extends RuntimeException {
    public PaymentClientErrorException(final String message) {
        super(message);
    }
}
