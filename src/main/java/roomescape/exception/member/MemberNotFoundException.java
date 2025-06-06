package roomescape.exception.member;

import org.springframework.http.HttpStatus;
import roomescape.exception.ApplicationException;

public class MemberNotFoundException extends ApplicationException {

    private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;
    private static final String MESSAGE = "존재하지 않는 회원입니다.";

    public MemberNotFoundException() {
        super(MESSAGE, STATUS);
    }
}
