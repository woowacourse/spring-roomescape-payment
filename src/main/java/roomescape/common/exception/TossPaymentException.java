package roomescape.common.exception;

import org.springframework.http.HttpStatus;

public class TossPaymentException extends RuntimeException {

    private final HttpStatus status;

    public TossPaymentException(TossPaymentExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        status = exceptionCode.getHttpStatus();
    }

    public HttpStatus getStatus() {
        return status;
    }
}
