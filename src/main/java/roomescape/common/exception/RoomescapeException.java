package roomescape.common.exception;

public class RoomescapeException extends RuntimeException {

    public RoomescapeException(final String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
