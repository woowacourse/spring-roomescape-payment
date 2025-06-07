package roomescape.member.exception;

import roomescape.common.exception.BusinessException;

public class MemberException extends BusinessException {

    public MemberException(String message) {
        super(message);
    }
}
