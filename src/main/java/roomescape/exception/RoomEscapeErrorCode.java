package roomescape.exception;

public enum RoomEscapeErrorCode {
    PAYMENT_FAILED("결제에 실패했습니다."),
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
    ;
    private final String message;

    RoomEscapeErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return name();
    }
}
