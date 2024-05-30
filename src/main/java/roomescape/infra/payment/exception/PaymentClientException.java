package roomescape.infra.payment.exception;

import org.springframework.http.HttpStatusCode;
import roomescape.exception.ApplicationException;

public class PaymentClientException extends ApplicationException {

    private final HttpStatusCode statusCode;

    public PaymentClientException(String message, HttpStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
