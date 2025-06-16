package roomescape.common.security.exception;

import roomescape.common.exception.RoomescapeException;

public class ForbiddenException extends RoomescapeException {

    public ForbiddenException(final String message) {
        super(message);
    }
}
