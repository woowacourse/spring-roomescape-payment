package roomescape.payment.exception;

public class PaymentTemporaryException extends PaymentServerException {
    public PaymentTemporaryException(String message) {
        super(message);
    }
}
