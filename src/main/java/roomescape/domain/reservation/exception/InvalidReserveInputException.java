package roomescape.domain.reservation.exception;

public class InvalidReserveInputException extends RuntimeException {

    public InvalidReserveInputException(final String message) {
        super(message);
    }
}
