package roomescape.exception;

public class PaymentException extends RuntimeException {

    protected final String code;

    protected final String message;

    public PaymentException(ExceptionResponse exceptionResponse) {
        super(exceptionResponse.message());
        this.code = exceptionResponse.code();
        this.message = exceptionResponse.message();
    }

    public ExceptionResponse toExceptionResponse() {
        return new ExceptionResponse(code, message);
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
