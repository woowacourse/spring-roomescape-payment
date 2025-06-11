package roomescape.exception.auth;

import org.springframework.http.HttpStatus;
import roomescape.exception.SeparatedMessageException;

public class InvalidPasswordException extends SeparatedMessageException {

    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;
    private static final String MESSAGE = "비밀번호가 일치하지 않습니다.";
    private static final String CLIENT_MESSAGE = "이메일 또는 비밀번호가 올바르지 않습니다.";

    public InvalidPasswordException() {
        super(STATUS, MESSAGE, CLIENT_MESSAGE);
    }
}
