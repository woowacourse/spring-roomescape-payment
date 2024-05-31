package roomescape.common.exception;

import org.springframework.http.HttpStatus;

public class PaymentException extends RuntimeException {

    private final HttpStatus status;

    public PaymentException(PaymentExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        status = exceptionCode.getHttpStatus();
    }

    public HttpStatus getStatus() {
        return status;
    }
}
