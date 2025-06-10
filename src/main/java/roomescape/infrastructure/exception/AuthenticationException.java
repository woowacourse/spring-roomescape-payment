package roomescape.infrastructure.exception;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException(final String message) {
        super(message);
    }
}
