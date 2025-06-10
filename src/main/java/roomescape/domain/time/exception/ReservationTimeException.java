package roomescape.domain.time.exception;

import roomescape.infrastructure.exception.DomainException;

public class ReservationTimeException extends DomainException {
    public ReservationTimeException(final String message) {
        super(message);
    }
}
