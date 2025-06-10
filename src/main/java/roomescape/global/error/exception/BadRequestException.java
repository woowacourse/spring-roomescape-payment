package roomescape.global.error.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends WarningException {

    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
