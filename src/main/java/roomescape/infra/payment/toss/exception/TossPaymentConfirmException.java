package roomescape.infra.payment.toss.exception;

import org.springframework.http.HttpStatusCode;
import roomescape.exception.ApplicationException;

public class TossPaymentConfirmException extends ApplicationException {

    private final HttpStatusCode statusCode;

    public TossPaymentConfirmException(String message, HttpStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
