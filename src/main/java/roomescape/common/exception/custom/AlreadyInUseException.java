package roomescape.common.exception.custom;

public class AlreadyInUseException extends RuntimeException {

    public AlreadyInUseException(final String message) {
        super(message);
    }
}
