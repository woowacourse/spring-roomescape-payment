package roomescape.exception;

public class RoomEscapeException extends RuntimeException{

    private final ErrorCode errorCode;
    private String detail;

    public RoomEscapeException(ErrorCode errorCode, String detail) {
        this.errorCode = errorCode;
        this.detail = detail;
    }

    public RoomEscapeException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getDetail() {
        return detail;
    }
}
