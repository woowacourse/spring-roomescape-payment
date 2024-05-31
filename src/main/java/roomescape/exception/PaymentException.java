package roomescape.exception;

import org.springframework.http.HttpStatusCode;

public class PaymentException extends RuntimeException {

    private final String errorMessage;
    private final HttpStatusCode httpStatusCode;

    public PaymentException(PaymentExceptionType paymentExceptionType, Throwable cause) {
        super(cause);
        this.httpStatusCode = paymentExceptionType.getHttpStatus();
        this.errorMessage = paymentExceptionType.getMessage();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }
}
