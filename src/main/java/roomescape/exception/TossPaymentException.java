package roomescape.exception;

import java.net.SocketTimeoutException;

public class TossPaymentException extends RuntimeException {
    public TossPaymentException(String message) {
        // INVALID_API_KEY
        // UNAUTHORIZED_KEY
        // todo: 사용자에게 보여주지 않아도 되는 값 필터링하기
        super(message);
    }

    public static boolean isTimeoutException(Throwable exception) {
        while (exception != null) {
            if (exception instanceof SocketTimeoutException) {
                return true;
            }
            exception = exception.getCause();
        }
        return false;
    }
}
