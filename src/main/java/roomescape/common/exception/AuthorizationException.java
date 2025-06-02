package roomescape.common.exception;

public class AuthorizationException extends CustomException {

    public AuthorizationException() {
        super("권한이 부족합니다.", GenaralErrorCode.FORBIDDEN);
    }

    public AuthorizationException(String message, GenaralErrorCode genaralErrorCode) {
        super(message, genaralErrorCode);
    }
}
