package roomescape.exception.model;

import org.springframework.http.HttpStatus;
import roomescape.exception.ExceptionCode;

public enum InternalExceptionCode implements ExceptionCode {

    INVALID_JSON_DATA(HttpStatus.INTERNAL_SERVER_ERROR, "Json 변환에 실패하였습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    InternalExceptionCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return null;
    }

    @Override
    public String getMessage() {
        return null;
    }
}
