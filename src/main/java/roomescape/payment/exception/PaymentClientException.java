package roomescape.payment.exception;

import org.springframework.http.HttpStatusCode;

public class PaymentClientException extends RuntimeException {

    private final HttpStatusCode statusCode;

    public PaymentClientException(HttpStatusCode statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
