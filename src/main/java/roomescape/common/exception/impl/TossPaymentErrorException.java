package roomescape.common.exception.impl;

import org.springframework.http.HttpStatus;

public class TossPaymentErrorException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public TossPaymentErrorException(final HttpStatus status, final String code, final String message) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }
}
