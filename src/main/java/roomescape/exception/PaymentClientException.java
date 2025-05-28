package roomescape.exception;

public class PaymentClientException extends RuntimeException {

    public PaymentClientException() {
    }

    public PaymentClientException(String message) {
        super(message);
    }
}
