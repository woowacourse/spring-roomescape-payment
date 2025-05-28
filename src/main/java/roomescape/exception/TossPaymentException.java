package roomescape.exception;

public class TossPaymentException extends RuntimeException {
    public TossPaymentException(String message, String code) {
        // INVALID_API_KEY
        // UNAUTHORIZED_KEY
        // todo: 사용자에게 보여주지 않아도 되는 값 필터링하기
        super(message);
    }
}
