package roomescape.exception.common;

import org.springframework.http.HttpStatus;

public class InternalServerException extends RuntimeExceptionWithLog {

    public InternalServerException(String message, String logMessage) {
        super(message, "[INTERNAL_SERVER_ERROR]" + logMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
