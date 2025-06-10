package roomescape.common.exception.impl;

import roomescape.common.exception.RoomescapeException;

public class TokenExpiredException extends RoomescapeException {

    public TokenExpiredException(String message) {
        super(message);
    }
}
