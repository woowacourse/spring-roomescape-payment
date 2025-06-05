package roomescape.common.exception.custom;

public class PaymentServerException extends RuntimeException{

    public PaymentServerException(final String message) {
        super(message);
    }
}
