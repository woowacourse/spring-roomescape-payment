package roomescape.exception.type;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import org.springframework.http.HttpStatus;

public enum PaymentExceptionType {
    INVALID_API_KEY(BAD_REQUEST, "서버 내부에서 결제 연동 상의 문제가 발생하였습니다. 잠시 후에 다시 시도해주세요"),
    UNAUTHORIZED_KEY(UNAUTHORIZED, "서버 내부에서 결제 연동 상의 문제가 발생하였습니다. 잠시 후에 다시 시도해주세요");

    private final HttpStatus httpStatus;
    private final String message;

    PaymentExceptionType(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
