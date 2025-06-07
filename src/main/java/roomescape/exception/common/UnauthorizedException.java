package roomescape.exception.common;

public class UnauthorizedException extends RuntimeExceptionWithLog {
    public UnauthorizedException(String message, Long memberId) {
        super(message, String.format("[UNAUTHORIZED] 현재 사용자 id : %d, 실패 사유 : %s", memberId, message));
    }

    public UnauthorizedException(String message) {
        super(message, "[UNAUTHORIZED] 실패 사유 : " + message);
    }
}
