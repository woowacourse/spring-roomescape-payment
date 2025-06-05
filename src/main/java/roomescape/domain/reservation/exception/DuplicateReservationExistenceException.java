package roomescape.domain.reservation.exception;

import roomescape.infrastructure.exception.DuplicateException;

public class DuplicateReservationExistenceException extends DuplicateException {
    public DuplicateReservationExistenceException(final String message) {
        super(message);
    }
}
