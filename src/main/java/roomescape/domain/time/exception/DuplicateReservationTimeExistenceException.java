package roomescape.domain.time.exception;

import roomescape.infrastructure.exception.DuplicateException;

public class DuplicateReservationTimeExistenceException extends DuplicateException {
    public DuplicateReservationTimeExistenceException(final String message) {
        super(message);
    }
}
