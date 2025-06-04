package roomescape.common.exception;

import roomescape.common.exception.vo.ErrorCode;

public class UnauthorizedException extends CustomException {
    public UnauthorizedException(String detail) {
        super(ErrorCode.UNAUTHORIZED, detail);
    }
}
