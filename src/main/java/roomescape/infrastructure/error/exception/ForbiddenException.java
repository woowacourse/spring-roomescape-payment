package roomescape.infrastructure.error.exception;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException(final String message) {
        super(message);
    }

    public ForbiddenException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
