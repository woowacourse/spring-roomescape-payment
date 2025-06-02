package roomescape.global.exception.authentication;

import org.springframework.http.HttpStatus;

public enum AuthenticationErrorStatus {
    PASSWORD_DOES_NOT_MATCH(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    ;

    public final HttpStatus httpStatus;
    public final String errorMessage;

    AuthenticationErrorStatus(HttpStatus httpStatus, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }
}
