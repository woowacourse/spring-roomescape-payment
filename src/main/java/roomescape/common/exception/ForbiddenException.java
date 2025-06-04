package roomescape.common.exception;

import roomescape.common.exception.vo.ErrorCode;

public class ForbiddenException extends CustomException {
    public ForbiddenException(String detail) {
        super(ErrorCode.FORBIDDEN, detail);
    }
}
