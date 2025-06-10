package roomescape.global.error.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends WarningException {

    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
} 
