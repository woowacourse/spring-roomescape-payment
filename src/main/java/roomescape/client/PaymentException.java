package roomescape.client;

import org.springframework.http.HttpStatusCode;

public class PaymentException extends RuntimeException {
    private final HttpStatusCode statusCode;

    public PaymentException(TossErrorResponse tossErrorResponse, HttpStatusCode statusCode) {
        super(tossErrorResponse.message());
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
