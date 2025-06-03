package roomescape.payment.global.exception;

import org.springframework.http.HttpStatus;

public class InvalidPaymentException extends RuntimeException {

    private final HttpStatus status;

    private static final String DEFAULT_MESSAGE = "유효하지 않는 결제 요청입니다.";

    public InvalidPaymentException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public InvalidPaymentException(HttpStatus status) {
        this(DEFAULT_MESSAGE, status);
    }

    public HttpStatus getStatus() {
        return status;
    }
}
