package roomescape.exception.auth;

import org.springframework.http.HttpStatus;
import roomescape.exception.ApplicationException;

public class AuthenticationExpiredException extends ApplicationException {

    private static final HttpStatus STATUS = HttpStatus.UNAUTHORIZED;
    private static final String MESSAGE = "인증이 만료되었습니다.";

    public AuthenticationExpiredException() {
        super(MESSAGE, STATUS);
    }
}
