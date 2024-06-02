package roomescape.exception;

import org.springframework.http.HttpStatus;

public class PaymentException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final PaymentExceptionResponse paymentExceptionResponse;

    public PaymentException(HttpStatus httpStatus, PaymentExceptionResponse paymentExceptionResponse) {
        this.httpStatus = httpStatus;
        this.paymentExceptionResponse = paymentExceptionResponse;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public PaymentExceptionResponse getPaymentExceptionResponse() {
        return paymentExceptionResponse;
    }
}
