package roomescape.domain.auth.exception;

import roomescape.infrastructure.exception.AuthenticationException;

public class MissingTokenException extends AuthenticationException {
    public MissingTokenException(final String message) {
        super(message);
    }
}
