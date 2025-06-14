package roomescape.global.error.exception;

public class InvalidReservationException extends BadRequestException {

    public InvalidReservationException(String message) {
        super(message);
    }
}
