package roomescape.exception;

public enum TossPaymentErrorCode {
    PAYMENT_FAILED("결제에 실패했습니다."),
    CONNECT_TIMEOUT("토스 결제 서버 연결에 실패했습니다"),
    READ_TIMEOUT("토스 결제 서버 내부에서 알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");

    private final String message;

    TossPaymentErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return name();
    }
}
