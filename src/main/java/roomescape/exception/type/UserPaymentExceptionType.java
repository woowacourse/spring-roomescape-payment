package roomescape.exception.type;

public enum UserPaymentExceptionType {
    INVALID_API_KEY("서버 내부에서 결제 연동 상의 문제가 발생하였습니다. 잠시 후에 다시 시도해주세요"),
    UNAUTHORIZED_KEY("서버 내부에서 결제 연동 상의 문제가 발생하였습니다. 잠시 후에 다시 시도해주세요");

    private final String message;

    UserPaymentExceptionType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
