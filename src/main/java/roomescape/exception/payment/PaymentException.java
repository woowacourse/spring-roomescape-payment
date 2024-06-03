package roomescape.exception.payment;

public class PaymentException extends RuntimeException {

    public PaymentException(String message) {
        super(message);
    }

    public PaymentException() {
        super("결제 서버 요청에 실패했습니다.");
    }
}
