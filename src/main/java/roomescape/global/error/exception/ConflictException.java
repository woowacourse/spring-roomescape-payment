package roomescape.global.error.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends WarningException {

    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
