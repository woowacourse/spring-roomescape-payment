package roomescape.exception.auth;

import org.springframework.http.HttpStatus;
import roomescape.exception.ApplicationException;

public class InvalidTokenException extends ApplicationException {

    private static final HttpStatus STATUS = HttpStatus.UNAUTHORIZED;
    private static final String MESSAGE = "잘못된 토큰입니다.";

    public InvalidTokenException() {
        super(MESSAGE, STATUS);
    }
}
