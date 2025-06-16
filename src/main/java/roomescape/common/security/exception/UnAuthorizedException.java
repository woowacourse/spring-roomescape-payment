package roomescape.common.security.exception;

import roomescape.common.exception.RoomescapeException;

public class UnAuthorizedException extends RoomescapeException {

    public UnAuthorizedException(final String message) {
        super(message);
    }
}
