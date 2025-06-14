package roomescape.global.error.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends WarningException {

    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
