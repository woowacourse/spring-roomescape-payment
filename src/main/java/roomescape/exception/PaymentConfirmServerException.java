package roomescape.exception;

public class PaymentConfirmServerException extends RuntimeException {

    public PaymentConfirmServerException() {
        super("결제에 실패했습니다. 잠시 후 다시 시도해주세요.");
    }

    public PaymentConfirmServerException(String message) {
        super(message);
    }
}
