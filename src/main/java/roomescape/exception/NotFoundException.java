package roomescape.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(final String message) {
        super("[ERROR] " + message);
    }
}
