package roomescape.common.exception;

import roomescape.common.exception.error.GeneralErrorCode;

public class BadRequestException extends BusinessException {

    public BadRequestException() {
        super("잘못된 입력입니다.", GeneralErrorCode.BAD_REQUEST);
    }

    public BadRequestException(String message) {
        super(message, GeneralErrorCode.BAD_REQUEST);
    }
}
