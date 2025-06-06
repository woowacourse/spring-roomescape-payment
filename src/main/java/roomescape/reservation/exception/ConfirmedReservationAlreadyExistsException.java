package roomescape.reservation.exception;

public class ConfirmedReservationAlreadyExistsException extends RuntimeException {

    public ConfirmedReservationAlreadyExistsException(String message) {
        super(message);
    }
}
