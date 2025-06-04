package roomescape.common.exception;

import roomescape.common.exception.vo.ErrorCode;

public class InternalServerErrorException extends CustomException {

    public InternalServerErrorException() {
        super(ErrorCode.SERVER_ERROR);
    }
}
