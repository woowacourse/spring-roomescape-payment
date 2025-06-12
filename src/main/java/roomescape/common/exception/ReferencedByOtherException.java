package roomescape.common.exception;

import roomescape.common.exception.error.GeneralErrorCode;

public class ReferencedByOtherException extends BusinessException {

    public ReferencedByOtherException(String message) {
        super(message, GeneralErrorCode.CONFLICT);
    }

    public ReferencedByOtherException(String message, GeneralErrorCode generalErrorCode) {
        super(message, generalErrorCode);
    }
}
