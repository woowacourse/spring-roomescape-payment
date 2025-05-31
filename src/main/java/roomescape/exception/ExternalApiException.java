package roomescape.exception;

import lombok.Getter;
import roomescape.exception.code.ErrorCode;

@Getter
public class ExternalApiException extends RuntimeException {
    private final ErrorCode errorCode;

    public ExternalApiException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ExternalApiException(ErrorCode errorCode, String message) {
        super(errorCode.getMessage() + ": " + message);
        this.errorCode = errorCode;
    }
}
