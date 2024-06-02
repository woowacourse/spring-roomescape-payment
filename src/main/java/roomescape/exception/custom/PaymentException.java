package roomescape.exception.custom;

public class PaymentException extends RuntimeException{

    public PaymentException(String message) {
        super(message);
    }
}
