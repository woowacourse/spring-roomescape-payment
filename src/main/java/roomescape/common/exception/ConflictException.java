package roomescape.common.exception;

import roomescape.common.exception.vo.ErrorCode;

public class ConflictException extends CustomException {
    public ConflictException(String detail) {
        super(ErrorCode.CONFLICT, detail);
    }
}
