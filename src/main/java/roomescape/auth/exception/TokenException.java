package roomescape.auth.exception;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import roomescape.exception.ExceptionCode;

@Tag(name = "토큰 예외", description = "토큰과 관련된 예외 코드를 보관한다.")
public enum TokenException implements ExceptionCode {

    NOT_SIGNED_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "서명된 토큰이 아닙니다."),
    FAILED_PARSE_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "잘못된 형식의 토큰으로 해석에 실패했습니다."),
    TOKEN_IS_EMPTY_EXCEPTION(HttpStatus.UNAUTHORIZED, "토큰의 값이 없습니다."),
    TOKEN_IS_EXPIRED_EXCEPTION(HttpStatus.UNAUTHORIZED, "토큰의 시간이 만료되었습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    TokenException(HttpStatus httpStatus, String message) {
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
