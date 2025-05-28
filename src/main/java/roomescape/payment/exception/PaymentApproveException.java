package roomescape.payment.exception;

import org.springframework.http.HttpStatusCode;

public class PaymentApproveException extends RuntimeException {

    private final HttpStatusCode httpStatusCode;

    public PaymentApproveException(final String message, final HttpStatusCode httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }
}
