package roomescape.infrastructure.error.exception;

public class JwtExtractException extends RuntimeException {

    public JwtExtractException(final String message) {
        super(message);
    }

    public JwtExtractException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
