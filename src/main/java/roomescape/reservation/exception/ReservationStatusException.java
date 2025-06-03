package roomescape.reservation.exception;

import roomescape.common.exception.DomainStatusException;

public class ReservationStatusException extends DomainStatusException {

    public ReservationStatusException(final String message) {
        super(message);
    }
}
