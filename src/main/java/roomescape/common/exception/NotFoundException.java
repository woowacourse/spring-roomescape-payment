package roomescape.common.exception;

import roomescape.common.exception.error.GeneralErrorCode;

public class NotFoundException extends BusinessException {

    public NotFoundException() {
        super("요청하신 자원을 찾을 수 없습니다.", GeneralErrorCode.NOT_FOUND);
    }

    public NotFoundException(String message, Object... args) {
        super(String.format(message, args), GeneralErrorCode.NOT_FOUND);
    }

    public NotFoundException(String message) {
        super(message, GeneralErrorCode.NOT_FOUND);
    }

    public NotFoundException(String message, GeneralErrorCode generalErrorCode) {
        super(message, generalErrorCode);
    }
}
