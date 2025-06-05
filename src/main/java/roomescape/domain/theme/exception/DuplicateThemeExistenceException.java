package roomescape.domain.theme.exception;

import roomescape.infrastructure.exception.DuplicateException;

public class DuplicateThemeExistenceException extends DuplicateException {
    public DuplicateThemeExistenceException(final String message) {
        super(message);
    }
}
