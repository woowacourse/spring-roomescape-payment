package roomescape.common.exception.custom;

public class LoginFailException extends RuntimeException {

    public LoginFailException(final String message) {
        super(message);
    }
}
