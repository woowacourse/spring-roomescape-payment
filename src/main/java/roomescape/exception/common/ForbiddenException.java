package roomescape.exception.common;

public class ForbiddenException extends RuntimeExceptionWithLog {

    public ForbiddenException(String message, String logMessage) {
        super(message, "[FORBIDDEN]" + logMessage);
    }

    public ForbiddenException(String message) {
        super(message, "[요청 처리 실패] 사유 : " + message);
    }
}
