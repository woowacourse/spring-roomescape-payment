package roomescape.common.exception;

import roomescape.common.exception.error.GeneralErrorCode;

public class AlreadyExistException extends BusinessException {

    public AlreadyExistException(String message) {
        super(message, GeneralErrorCode.CONFLICT);
    }
}
