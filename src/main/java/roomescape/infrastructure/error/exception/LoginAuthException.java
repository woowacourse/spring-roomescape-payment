package roomescape.infrastructure.error.exception;

public class LoginAuthException extends UnauthorizedException {

    public LoginAuthException(final String message) {
        super(message);
    }

    public LoginAuthException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
