package roomescape.exception;

import org.springframework.http.HttpStatus;
import roomescape.dto.response.reservation.TossExceptionResponse;

public class PaymentException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final TossExceptionResponse tossExceptionResponse;

    public PaymentException(HttpStatus httpStatus, TossExceptionResponse tossExceptionResponse) {
        super(tossExceptionResponse.message());
        this.httpStatus = httpStatus;
        this.tossExceptionResponse = tossExceptionResponse;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public TossExceptionResponse getTossExceptionResponse() {
        return tossExceptionResponse;
    }
}
