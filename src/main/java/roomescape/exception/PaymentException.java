package roomescape.exception;

import org.springframework.http.HttpStatusCode;

public class PaymentException extends RuntimeException {

    private final HttpStatusCode httpStatusCode;
    private final String errorMessage;

    public PaymentException(HttpStatusCode httpStatusCode, String errorMessage, Throwable cause) {
        super(cause);
        this.httpStatusCode = httpStatusCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }
}
