package roomescape.payment.application;

public class PaymentServerException extends RuntimeException {
    private final String message;

    public PaymentServerException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
