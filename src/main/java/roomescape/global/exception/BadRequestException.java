package roomescape.global.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BusinessException {

    public BadRequestException(String message) {
        super(new ErrorCode(HttpStatus.BAD_REQUEST, "BAD_REQUEST", message));
    }
}
