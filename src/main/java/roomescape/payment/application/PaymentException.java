package roomescape.payment.application;

public class PaymentException extends RuntimeException {
    public PaymentException(String message) {
        super(message);
    }
}
