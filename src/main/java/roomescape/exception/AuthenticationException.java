package roomescape.exception;

public class AuthenticationException extends RoomEscapeException {
    public AuthenticationException() {
        super("인증에 실패하였습니다");
    }
}
