package roomescape.global.exception;

import org.springframework.http.HttpStatus;

public class AccessDeniedException extends BusinessException {
    
    public AccessDeniedException(final String message) {
        super(new ErrorCode(HttpStatus.FORBIDDEN, message));
    }
}
