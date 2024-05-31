package roomescape.auth.exception;

public class ExpiredTokenException extends UnauthenticatedUserException {

    public ExpiredTokenException(final String message) {
        super(message);
    }
}
