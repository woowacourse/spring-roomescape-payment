package roomescape.domain.theme.exception;

import roomescape.infrastructure.exception.DomainException;

public class ThemeException extends DomainException {
    public ThemeException(final String message) {
        super(message);
    }
}
