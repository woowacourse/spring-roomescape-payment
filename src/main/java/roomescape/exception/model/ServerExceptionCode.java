package roomescape.exception.model;

import org.springframework.http.HttpStatus;
import roomescape.exception.ExceptionCode;

public enum ServerExceptionCode implements ExceptionCode {
    RESERVATION_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "서버 문제로 예약에 실패하였습니다."),
    RESERVATION_URI_MOVE(HttpStatus.FOUND, "예약 주소가 바뀌었습니다. 새로운 예약 주소로 다시 시도해 주세요.");

    private final HttpStatus httpStatus;
    private final String message;

    ServerExceptionCode(HttpStatus httpStatus, String message) {
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
