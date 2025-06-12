package roomescape.exception.auth;

public class JwtExtractException extends RuntimeException {

    public JwtExtractException(String message) {
        super(message);
    }
}
