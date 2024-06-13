package roomescape.exception;

public class RoomescapeException extends RuntimeException {
    private final ExceptionResponse exceptionResponse;

    public RoomescapeException(String message) {
        super(message);
        this.exceptionResponse = new ExceptionResponse(message);
    }

    public ExceptionResponse getRoomescapeExceptionResponse() {
        return exceptionResponse;
    }
}
