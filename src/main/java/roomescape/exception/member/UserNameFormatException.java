package roomescape.exception.member;

import org.springframework.http.HttpStatus;
import roomescape.exception.ApplicationException;

public class UserNameFormatException extends ApplicationException {

    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;
    private static final String MESSAGE = "유저 이름에 숫자는 포함될 수 없습니다.";

    public UserNameFormatException() {
        super(MESSAGE, STATUS);
    }
}
