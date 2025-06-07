package roomescape.exception.common;

public class InternalServerException extends RuntimeExceptionWithLog {

    public InternalServerException(String message, String logMessage) {
        super(message, "[INTERNAL_SERVER_ERROR]" + logMessage);
    }
}
