package roomescape.common.exception;

public class AuthenticationException extends CustomException {

    public AuthenticationException(String message) {
        super(message, GenaralErrorCode.UNAUTHORIZED);
    }

    public AuthenticationException(String message, GenaralErrorCode genaralErrorCode) {
        super(message, genaralErrorCode);
    }
}
