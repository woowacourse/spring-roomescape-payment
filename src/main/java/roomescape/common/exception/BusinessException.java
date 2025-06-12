package roomescape.common.exception;

import roomescape.common.exception.error.ErrorCode;

public class BusinessException extends CustomException {

    protected BusinessException(final String message, final ErrorCode errorCode) {
        super(message, errorCode);
    }
}
