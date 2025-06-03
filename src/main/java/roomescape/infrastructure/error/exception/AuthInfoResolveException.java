package roomescape.infrastructure.error.exception;

public class AuthInfoResolveException extends RuntimeException {

    public AuthInfoResolveException(final String message) {
        super(message);
    }

    public AuthInfoResolveException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
