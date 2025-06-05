package roomescape.auth.exception;

public class TokenCreationException extends RuntimeException {

    public TokenCreationException(final String message) {
        super(message);
    }
}
