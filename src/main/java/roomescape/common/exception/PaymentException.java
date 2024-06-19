package roomescape.common.exception;

public class PaymentException extends RuntimeException {

    public PaymentException(final String message) {
        super(message);
    }

    public PaymentException() {
        super("결제가 취소되었습니다.");
    }
}
