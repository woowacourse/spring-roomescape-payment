package roomescape.common.exception;

import roomescape.common.exception.error.GeneralErrorCode;

public class InvalidInputException extends BusinessException {

    public InvalidInputException(String message) {
        super(message, GeneralErrorCode.INVALID_INPUT);
    }
}
