package roomescape.common.exception;

import roomescape.common.exception.error.GeneralErrorCode;

public class AuthorizationException extends BusinessException {

    public AuthorizationException() {
        super("권한이 부족합니다.", GeneralErrorCode.FORBIDDEN);
    }

    public AuthorizationException(String message, GeneralErrorCode generalErrorCode) {
        super(message, generalErrorCode);
    }
}
