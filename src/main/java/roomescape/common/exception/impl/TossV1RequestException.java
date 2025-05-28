package roomescape.common.exception.impl;

import org.springframework.http.HttpStatus;

public class TossV1RequestException extends RuntimeException{

    private final HttpStatus status;
    private final String code;

    public TossV1RequestException(final HttpStatus status, final String code, final String message) {
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
