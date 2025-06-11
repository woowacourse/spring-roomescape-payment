package roomescape.exception.auth;

import org.springframework.http.HttpStatus;
import roomescape.exception.ApplicationException;

public class AuthenticationRequiredException extends ApplicationException {

    private static final HttpStatus STATUS = HttpStatus.UNAUTHORIZED;
    private static final String MESSAGE = "로그인이 필요합니다.";

    public AuthenticationRequiredException() {
        super(MESSAGE, STATUS);
    }
}
