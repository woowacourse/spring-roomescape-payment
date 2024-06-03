package roomescape.exception;

public class RoomescapeException extends RuntimeException {

    private final RoomescapeErrorCode errorCode;
    private final String message;
    private final Throwable cause;

    public RoomescapeException(RoomescapeErrorCode errorCode) {
        this(errorCode, null, null);
    }

    public RoomescapeException(RoomescapeErrorCode errorCode, String message) {
        this(errorCode, message, null);
    }

    public RoomescapeException(RoomescapeErrorCode errorCode, String message, Throwable cause) {
        this.errorCode = errorCode;
        this.message = message;
        this.cause = cause;
    }

    public RoomescapeErrorCode getErrorCode() {
        return errorCode;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    @Override
    public String getMessage() {
        if (message == null) {
            return errorCode.getMessage();
        }
        return message;
    }
}
