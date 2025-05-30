package roomescape.infrastructure.error.exception;

public class TossPaymentException extends PaymentException {

    private static final String PREFIX = "토스 결제 오류: %s";

    public TossPaymentException(String message) {
        super(PREFIX.formatted(message));
    }
}
