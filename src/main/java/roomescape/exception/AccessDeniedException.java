package roomescape.exception;

import org.springframework.http.HttpStatus;

public class AccessDeniedException extends CustomException{

    public AccessDeniedException(final String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
