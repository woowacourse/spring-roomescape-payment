package roomescape.common.exception;

import roomescape.common.exception.vo.ErrorCode;

public class NotFoundException extends CustomException {

    public NotFoundException(String detail) {
        super(ErrorCode.NOT_FOUND, detail);
    }

    public NotFoundException(String resource, Long id) {
        this(String.format("%d 식별자를 갖는 %s(이)가 존재하지 않습니다.", id, resource));
    }
}
