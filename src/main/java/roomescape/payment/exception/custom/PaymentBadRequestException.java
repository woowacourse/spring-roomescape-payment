package roomescape.payment.exception.custom;

public class PaymentBadRequestException extends RuntimeException {
    public PaymentBadRequestException(final String message) {
        super(message);
    }
}
