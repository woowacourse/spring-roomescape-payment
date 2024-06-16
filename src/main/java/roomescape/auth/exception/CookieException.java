package roomescape.auth.exception;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import roomescape.exception.ExceptionCode;

@Tag(name = "쿠키 예외", description = "쿠키와 관련된 예외 코드를 보관한다.")
public enum CookieException implements ExceptionCode {

    COOKIE_IS_NULL_EXCEPTION(HttpStatus.UNAUTHORIZED, "쿠키가 비어있습니다."),
    COOKE_VALUE_IS_NOT_FOUND_EXCEPTION(HttpStatus.UNAUTHORIZED, "찾으려는 쿠키 값이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    CookieException(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
