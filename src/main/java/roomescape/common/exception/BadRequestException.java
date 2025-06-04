package roomescape.common.exception;

import roomescape.common.exception.vo.ErrorCode;

public class BadRequestException extends CustomException {
    public BadRequestException(String detail) {
        super(ErrorCode.BAD_REQUEST, detail);
    }
}
