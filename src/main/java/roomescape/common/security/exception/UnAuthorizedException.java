package roomescape.common.security.exception;

import roomescape.common.exception.BusinessException;

public class UnAuthorizedException extends BusinessException {

    public UnAuthorizedException(final String message) {
        super(message);
    }
}
