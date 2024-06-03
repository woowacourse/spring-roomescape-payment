package roomescape.exception;

import org.springframework.http.HttpStatus;
import roomescape.dto.response.reservation.PaymentExceptionResponse;

public class PaymentException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final PaymentExceptionResponse paymentExceptionResponse;

    public PaymentException(HttpStatus httpStatus, PaymentExceptionResponse paymentExceptionResponse) {
        super(paymentExceptionResponse.message());
        this.httpStatus = httpStatus;
        this.paymentExceptionResponse = paymentExceptionResponse;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public PaymentExceptionResponse getTossExceptionResponse() {
        return paymentExceptionResponse;
    }
}
