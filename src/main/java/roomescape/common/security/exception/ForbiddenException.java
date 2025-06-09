package roomescape.common.security.exception;

import roomescape.common.exception.BusinessException;

public class ForbiddenException extends BusinessException {

    public ForbiddenException(final String message) {
        super(message);
    }
}
