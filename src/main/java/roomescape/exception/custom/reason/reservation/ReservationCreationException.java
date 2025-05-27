package roomescape.exception.custom.reason.reservation;

import roomescape.exception.custom.status.BadRequestException;

public class ReservationCreationException extends BadRequestException {
    public ReservationCreationException(String message) {
        super(message);
    }
}
