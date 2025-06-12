package roomescape.exception.common;

import org.springframework.http.HttpStatus;

public class RuntimeExceptionWithLog extends RuntimeException{

    private final String logMessage;
    private final HttpStatus httpStatus;

    public RuntimeExceptionWithLog(String message, String logMessage, HttpStatus httpStatus) {
        super(message);
        this.logMessage = logMessage;
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getLogMessage() {
        return logMessage;
    }
}
