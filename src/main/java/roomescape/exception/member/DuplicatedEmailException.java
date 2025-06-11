package roomescape.exception.member;

import org.springframework.http.HttpStatus;
import roomescape.exception.ApplicationException;

public class DuplicatedEmailException extends ApplicationException {

    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;
    private static final String MESSAGE = "중복된 이메일입니다.";

    public DuplicatedEmailException() {
        super(MESSAGE, STATUS);
    }

}
