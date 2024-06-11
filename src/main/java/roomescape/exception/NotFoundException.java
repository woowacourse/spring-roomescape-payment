package roomescape.exception;

public class NotFoundException extends RuntimeException {
    private final ExceptionResponse exceptionResponse;

    public NotFoundException(String message) {
        super(message);
        this.exceptionResponse = new ExceptionResponse(message);
    }

    public ExceptionResponse getRoomescapeExceptionResponse() {
        return exceptionResponse;
    }
}
