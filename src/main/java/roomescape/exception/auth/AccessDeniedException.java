package roomescape.exception.auth;

import org.springframework.http.HttpStatus;
import roomescape.exception.ApplicationException;

public class AccessDeniedException extends ApplicationException {

    private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;
    private static final String MESSAGE = "이 작업을 수행할 권한이 없습니다.";

    public AccessDeniedException() {
        super(MESSAGE, STATUS);
    }
}
