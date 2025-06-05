package roomescape.domain.reservation.exception;

import roomescape.infrastructure.exception.DomainException;

public class ReservationException extends DomainException {
    public ReservationException(final String message) {
        super(message);
    }
}
