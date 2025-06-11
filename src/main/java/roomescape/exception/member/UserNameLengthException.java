package roomescape.exception.member;

import org.springframework.http.HttpStatus;
import roomescape.exception.ApplicationException;

public class UserNameLengthException extends ApplicationException {

    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;
    private static final String MESSAGE_FORMAT = "유저 이름은 %d자를 넘길 수 없습니다.";

    public UserNameLengthException(int length) {
        super(String.format(MESSAGE_FORMAT, length), STATUS);
    }

}
