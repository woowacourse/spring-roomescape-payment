package roomescape.global.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BusinessException {

    public NotFoundException(String message) {
        super(new ErrorCode(HttpStatus.NOT_FOUND, "NOT_FOUND", message));
    }
}
