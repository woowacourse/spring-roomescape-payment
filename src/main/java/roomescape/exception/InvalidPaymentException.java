package roomescape.exception;

public class InvalidPaymentException extends RuntimeException {
    public InvalidPaymentException() {
        super("결제 과정에서 문제가 발생하였습니다. 관리자에게 문의해주세요.");
    }

    public InvalidPaymentException(String message) {
        super(message);
    }

    public InvalidPaymentException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPaymentException(Throwable cause) {
        super(cause);
    }
}
