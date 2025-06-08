package roomescape.common.exception;

import org.springframework.boot.logging.LogLevel;
import roomescape.common.exception.vo.ErrorCode;

public class LoggableException extends RuntimeException {

    private final ErrorCode errorCode;
    private final LogLevel logLevel;

    public LoggableException(ErrorCode errorCode, LogLevel logLevel) {
        this.errorCode = errorCode;
        this.logLevel = logLevel;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }
}
