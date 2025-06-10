package roomescape.domain.auth.exception;

import roomescape.infrastructure.exception.AuthorizationException;

public class AccessDeniedException extends AuthorizationException {
    public AccessDeniedException(final String message) {
        super(message);
    }
}
