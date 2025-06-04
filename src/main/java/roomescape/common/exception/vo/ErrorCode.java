package roomescape.common.exception.vo;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스가 존재하지 않습니다."),
    CONFLICT(HttpStatus.CONFLICT, "중복되는 리소스가 이미 존재합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요한 서비스 입니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "요청을 처리하는 과정에서 오류가 발생했습니다."),
    ;

    public final HttpStatus status;
    public final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
