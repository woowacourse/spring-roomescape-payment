package roomescape.exception.common;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends RuntimeExceptionWithLog {

    public ForbiddenException(String message, String logMessage) {
        super(message, "[FORBIDDEN] 권한 부족 사유 : " + logMessage, HttpStatus.FORBIDDEN);
    }
}
