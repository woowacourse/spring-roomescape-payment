package roomescape.common.exception;

import org.springframework.http.HttpStatus;

public enum PaymentErrorCode implements ErrorCode {
    PAYMENT_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "결제 처리 중 알 수 없는 오류가 발생했습니다. 잠시 후 시도해주세요."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 문제로 API 요청 응답에 문제가 발생했습니다."),
    CLIENT_ERROR(HttpStatus.BAD_REQUEST, "사용자의 입력 문제로 결제 처리에 실패하였습니다.");

    private final HttpStatus status;
    private final String message;

    PaymentErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public int getStatusValue() {
        return status.value();
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
