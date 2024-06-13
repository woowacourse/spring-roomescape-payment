package roomescape.exception;

public class AuthorizedException extends RuntimeException {
    private final ExceptionResponse exceptionResponse;

    public AuthorizedException(String message) {
        super(message);
        this.exceptionResponse = new ExceptionResponse(message);
    }

    public ExceptionResponse getRoomescapeExceptionResponse() {
        return exceptionResponse;
    }
}
