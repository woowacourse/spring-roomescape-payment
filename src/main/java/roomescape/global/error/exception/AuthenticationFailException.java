package roomescape.global.error.exception;

public class AuthenticationFailException extends BadRequestException {

    public AuthenticationFailException(String message) {
        super(message);
    }
}
