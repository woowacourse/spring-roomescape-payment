package roomescape.advice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum ExceptionTitle {
    ILLEGAL_USER_REQUEST("유효하지 않은 요청 데이터입니다.", HttpStatus.BAD_REQUEST),
    AUTHENTICATION_FAILED("인증에 실패했습니다.", HttpStatus.UNAUTHORIZED),
    LACK_OF_AUTHORIZATION("권한이 부족합니다.", HttpStatus.FORBIDDEN),
    PAYMENT_FAILED("결제에 실패했습니다.", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("서버에 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final String title;
    private final HttpStatusCode statusCode;

    ExceptionTitle(String title, HttpStatusCode statusCode) {
        this.title = title;
        this.statusCode = statusCode;
    }

    public String getTitle() {
        return title;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
