package roomescape.common.exception;

import roomescape.common.exception.vo.ErrorCode;

public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode, String detail) {
        super(errorCode.message + ", 원인: " + detail);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
