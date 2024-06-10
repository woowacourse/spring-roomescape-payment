package roomescape.exception;

public class AuthenticationException extends RuntimeException {

    private final String code;

    private final ExceptionResponse exceptionResponse;

    public AuthenticationException(ErrorType errorType) {
        super(errorType.getMessage());
        this.code = errorType.getErrorCode();
        this.exceptionResponse = ExceptionResponse.of(errorType);
    }

    public String getCode() {
        return code;
    }

    public ExceptionResponse getExceptionResponse() {
        return exceptionResponse;
    }
}
