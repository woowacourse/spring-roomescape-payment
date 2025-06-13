package roomescape.global.exception;

public class ClientTimeoutException extends RuntimeException {

    public ClientTimeoutException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public static final class PaymentConnectionTimeoutException extends ClientTimeoutException {

        private static final String message = "결제 서버에 연결할 수 없습니다.";

        public PaymentConnectionTimeoutException(Throwable throwable) {
            super(message, throwable);
        }
    }

    public static final class PaymentReadTimeoutException extends ClientTimeoutException {

        private static final String message = "결제 처리시간이 초과되었습니다.";

        public PaymentReadTimeoutException(Throwable throwable) {
            super(message, throwable);
        }
    }

    public static final class PaymentTimeoutException extends ClientTimeoutException {

        private static final String message = "결제 시간이 초과되었습니다.";

        public PaymentTimeoutException(Throwable throwable) {
            super(message, throwable);
        }
    }
}
