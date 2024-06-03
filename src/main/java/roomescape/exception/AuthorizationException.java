package roomescape.exception;

public class AuthorizationException extends RoomEscapeException {
    public AuthorizationException() {
        super("권한이 없습니다.");
    }
}
