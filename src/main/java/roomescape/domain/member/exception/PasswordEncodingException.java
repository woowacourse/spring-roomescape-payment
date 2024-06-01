package roomescape.domain.member.exception;

public class PasswordEncodingException extends RuntimeException {

    public PasswordEncodingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
