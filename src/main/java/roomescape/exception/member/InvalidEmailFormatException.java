package roomescape.exception.member;

import org.springframework.http.HttpStatus;
import roomescape.exception.ApplicationException;

public class InvalidEmailFormatException extends ApplicationException {

    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;
    private static final String MESSAGE = "잘못된 이메일 형식입니다.";

    public InvalidEmailFormatException() {
        super(MESSAGE, STATUS);
    }
}
