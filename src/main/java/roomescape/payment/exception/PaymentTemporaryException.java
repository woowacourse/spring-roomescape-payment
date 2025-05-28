package roomescape.payment.exception;

public class PaymentTemporaryException extends RuntimeException {
    public PaymentTemporaryException(String message) {
        super(message);
    }
}
