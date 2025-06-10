package roomescape.global.error.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends WarningException {

    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
} 
