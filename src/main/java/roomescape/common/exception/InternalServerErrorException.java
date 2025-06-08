package roomescape.common.exception;

import org.springframework.boot.logging.LogLevel;
import roomescape.common.exception.vo.ErrorCode;

public class InternalServerErrorException extends LoggableException {

    private final String message;

    public InternalServerErrorException(String message) {
        super(ErrorCode.SERVER_ERROR, LogLevel.ERROR);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
