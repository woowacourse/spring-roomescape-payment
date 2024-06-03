package roomescape.exception;

public class PaymentServerException extends RoomEscapeException {
    public PaymentServerException() {
        super("결제에 실패했어요. 같은 문제가 반복된다면 관리자에게 문의해주세요.");
    }

    public PaymentServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
