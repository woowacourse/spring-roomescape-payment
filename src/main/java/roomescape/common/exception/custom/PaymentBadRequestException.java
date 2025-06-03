package roomescape.common.exception.custom;

public class PaymentBadRequestException extends RuntimeException{

    public PaymentBadRequestException(final String message) {
        super(message);
    }
}
