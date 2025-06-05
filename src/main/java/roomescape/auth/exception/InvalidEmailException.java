package roomescape.auth.exception;

public class InvalidEmailException extends AuthenticationException {

    public InvalidEmailException(final String message) {
        super(message);
    }
}
