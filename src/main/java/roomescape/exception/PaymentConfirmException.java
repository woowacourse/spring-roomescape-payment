package roomescape.exception;

import org.springframework.http.HttpStatus;

public class PaymentConfirmException extends RuntimeException{

    private final String failureCode;
    private final ExceptionCode exceptionCode;

    public PaymentConfirmException(String code, ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.failureCode = code;
        this.exceptionCode = exceptionCode;
    }

    public PaymentConfirmException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.failureCode = exceptionCode.getHttpStatus().toString();
        this.exceptionCode = exceptionCode;
    }

    public String getFailureCode() {
        return failureCode;
    }

    public HttpStatus getHttpStatus() {
        return exceptionCode.getHttpStatus();
    }

    @Override
    public String getMessage() {
        return exceptionCode.getMessage();
    }
}
