package roomescape.infra.payment.exception;

import org.springframework.http.HttpStatusCode;
import roomescape.exception.ApplicationException;

public class PaymentConfirmException extends ApplicationException {

    private final HttpStatusCode statusCode;

    public PaymentConfirmException(String message, HttpStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
