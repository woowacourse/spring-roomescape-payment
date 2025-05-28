package roomescape.common.exception;

public class PaymentBadRequestException extends RuntimeException{

    public PaymentBadRequestException(final String message) {
        super(message);
    }
}
