package roomescape.domain.waiting.exception;

import roomescape.infrastructure.exception.DuplicateException;

public class DuplicateWaitingExistenceException extends DuplicateException {
    public DuplicateWaitingExistenceException(final String message) {
        super(message);
    }
}
