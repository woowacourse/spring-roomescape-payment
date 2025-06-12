package roomescape.global.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends BusinessException {

    public AuthenticationException(final String message) {
        super(new ErrorCode(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", message));
    }
}
