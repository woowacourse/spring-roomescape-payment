package roomescape.reservation.exception;

public class InAlreadyReservationException extends RuntimeException {
    public InAlreadyReservationException(String message) {
        super(message);
    }
}
