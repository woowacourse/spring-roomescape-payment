package roomescape.exception.auth;

import org.springframework.http.HttpStatus;
import roomescape.exception.SeparatedMessageException;

public class EmailNotRegisteredException extends SeparatedMessageException {

    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;
    private static final String MESSAGE = "해당 이메일로 등록된 계정이 없습니다.";
    private static final String CLIENT_MESSAGE = "이메일 또는 비밀번호가 올바르지 않습니다.";

    public EmailNotRegisteredException() {
        super(STATUS, MESSAGE, CLIENT_MESSAGE);
    }
}
