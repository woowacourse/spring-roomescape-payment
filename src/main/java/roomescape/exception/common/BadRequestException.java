package roomescape.exception.common;

public class BadRequestException extends RuntimeExceptionWithLog {

    public BadRequestException(String message, String logMessage) {
        super(message, "[BAD_REQUEST]" + logMessage);
    }

    public BadRequestException(String message) {
        super(message, "[잘못된 요청] 실패 사유 : " + message);
    }
}
