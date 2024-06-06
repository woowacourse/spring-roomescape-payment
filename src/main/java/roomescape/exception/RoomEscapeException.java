package roomescape.exception;

public class RoomEscapeException extends RuntimeException {
    public RoomEscapeException(String message) {
        super(message);
    }

    protected RoomEscapeException(String message, Throwable cause) {
        super(message, cause);
    }
}
